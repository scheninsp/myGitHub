import numpy as np
from scipy.optimize import minimize
import copy
import cv2

WIDTH=114
HEIGHT=86

# neighbour for one pixel
DW = 4
DH = 4

FACE_AREAS = np.zeros((2,4),dtype = np.float32)
INDX_GRID = np.zeros ( (HEIGHT, WIDTH, 2), dtype = np.float32)  # indicate original index
U_GRID = np.zeros ( (HEIGHT, WIDTH, 2), dtype = np.float32)  # undistort grid using radial function
U_GRID_FLATTEN = U_GRID.flatten()

LAMBDA_F = 4
LAMBDA_R = 0.5


def distort_coords(coords, cx, cy, k1):

    height, width = coords.shape[:2]
    tmp1 = np.asarray([cx, cy], dtype=np.float32)
    tmp1 = np.tile(tmp1, (height, width, 1))

    tmp2 = coords - tmp1
    tmp3 = np.sum(tmp2 ** 2, axis=-1) * k1 + 1
    tmp3 = tmp3[..., np.newaxis]
    tmp4 = tmp2/tmp3 + tmp1

    return tmp4

def myfunc(coords):
    # - coords : 1 * (H * W * 2)
    # - face_areas : nFace * 4 (xmin, xmax, ymin, ymax)
    kFace = FACE_AREAS.shape[0]

    # term of || vi - ui ||^2 in face areas
    face_areas_indx_x = [[]]
    for k in range(kFace-1):
        face_areas_indx_x.append([])
    face_areas_indx_y = copy.deepcopy(face_areas_indx_x)
    for k in range(kFace):
        for i in range(FACE_AREAS[k, 2], FACE_AREAS[k, 3]):
            for j in range (FACE_AREAS[k, 0], FACE_AREAS[k, 1]):
                face_areas_indx_x[k].append(2 * WIDTH * i + 2 * j)
                face_areas_indx_y[k].append(2 * WIDTH * i + 2 * j + 1)

    ef = 0
    for k in range(kFace):
        ef += np.sum((coords[face_areas_indx_x[k]] - U_GRID_FLATTEN[face_areas_indx_x[k]]) ** 2)

    # term of || vi - vj ||^2
    er = 0
    for i in range(HEIGHT):
        for j in range(WIDTH):
            tmpx = 0
            tmpy = 0
            neighbour_area_indx_x = []
            neighbour_area_indx_y = []
            for i2 in range(max(0, i-DH),min(HEIGHT,i+DH+1)):
                for j2 in range(max(0, j-DW),min(WIDTH,j+DW+1)):
                    neighbour_area_indx_x.append(2 * WIDTH * i2 + 2 * j2)
                    neighbour_area_indx_y.append(2 * WIDTH * i2 + 2 * j2 + 1)

            lx = len(neighbour_area_indx_x)
            ly = len(neighbour_area_indx_y)
            tmpx += np.sum((coords[2 * WIDTH * i + 2 * j] * np.ones(lx, dtype=np.float32) -
                     coords[neighbour_area_indx_x]) ** 2)
            tmpy += np.sum((coords[2 * WIDTH * i + 2 * j + 1] * np.ones(ly, dtype=np.float32) -
                     coords[neighbour_area_indx_y]) ** 2)

            er += (tmpx + tmpy)

    energy = LAMBDA_F * ef + LAMBDA_R * er

    return energy
    
def myfunc_der(coords):
    # - coords 1*(2*W*H)
    # - face_areas : nFace * 4(xmin, xmax, ymin, ymax)
    
    if(len(coords) % 2 != 0):
        print("ERROR: length of coords must be even")
        return coords
        
    nvar = int(len(coords)/2)
    
    ef_der = np.zeros(2*nvar, dtype=np.float32)
    kFace = FACE_AREAS.shape[0]
    
    face_areas_indx = [[]]
    for k in range(kFace -1):
        face_areas_indx.append([])
    
    for k in range(kFace):
        for i in range(FACE_AREAS[k, 2], FACE_AREAS[k, 3]):
            for j in range(FACE_AREAS[k,0], FACE_AREAS[k,1]):
                face_areas_indx[k].append(2 * WIDTH * i + 2 * j)
                face_areas_indx[k].append(2 * WIDTH * i + 2 * j + 1)

    for k in range(kFace):
        ef_der[face_areas_indx[k]] = ef_der[face_areas_indx[k]] + \
        2 * (coords[face_areas_indx[k]] - U_GRID_FLATTEN[face_areas_indx[k]])
        
    er_der = np.zeros(2*nvar, dtype=np.float32)
    
    for i in range(HEIGHT):
        for j in range(WIDTH):
            
            tmpx=0
            tmpy=0
            neighbour_area_indx_x = []
            neighbour_area_indx_y = []
            
            for i2 in range(max(0, i-DH), min(HEIGHT, i+DH+1)):
                for j2 in range(max(0, j-DW), min(WIDTH, j+DW+1)):
                    neighbour_area_indx_x.append(2*WIDTH*i2+2*j2)
                    neighbour_area_indx_y.append(2*WIDTH*i2 + 2*j2 + 1)
            
            lx = len(neighbour_area_indx_x)
            ly = len(neighbour_area_indx_y)
            er_der[2*WIDTH*i + 2*j] = 4 * np.sum(coords[2*WIDTH*i + 2*j] * np.ones(lx, dtype=np.float32) - coords[neighbour_area_indx_x])
            er_der[2*WIDTH*i + 2*j + 1] = 4 * np.sum(coords[2*WIDTH*i + 2*j + 1] * np.ones(lx, dtype=np.float32) - coords[neighbour_area_indx_y])
            
    e_der = LAMBDA_F * ef_der + LAMBDA_R * er_der
    
    return e_der


def map_flat_to_grid(coords_flat,height,width):

    coords_grid = np.zeros((height,width,2),dtype=np.float32)
    for i in range(height):
        for j in range(width):
            coords_grid[i,j,0] = coords_flat[2 * width * i + 2 * j]
            coords_grid[i,j,1] = coords_flat[2 * width * i + 2 * j + 1]
    return coords_grid

def visualize_grid(coords_grid, output_filename):

    height, width = coords_grid.shape[:2]

    #prepare draw board
    pixel_inter = 20
    new_height = height*pixel_inter
    new_width = width*pixel_inter
    img_draw = 255 * np.ones((new_height, new_width, 3),np.uint8)

    #expand meshgrid
    coords_grid = coords_grid * pixel_inter
    coords_grid_mask = np.all ((coords_grid > [0,0]) & (coords_grid < [new_width, new_height]), axis=-1)

    color = (255,0,0)
    thick = 2
    for i in range(height):
        for j in range(width-1):
            if coords_grid_mask[i,j] and coords_grid_mask[i,j+1]:
                cv2.line(img_draw, tuple(coords_grid[i,j,:]),tuple(coords_grid[i,j+1,:]),
                     color, thick)

    for j in range(width):
        for i in range(height-1):
            if coords_grid_mask[i,j] and coords_grid_mask[i+1,j]:
                cv2.line(img_draw, tuple(coords_grid[i,j,:]),tuple(coords_grid[i+1,j,:]),
                     color, thick)

    cv2.imwrite(output_filename,img_draw)

if __name__ == "__main__":

    width = WIDTH
    height = HEIGHT

    cx = width/2
    cy = height/2
    k_scale = 1 / (cx**2 + cy**2)
    k1 = k_scale * 1.4

    face_areas = np.asarray([[10,30,10,40], [80,100,20,50]])

    x,y = np.meshgrid(np.linspace(0, width-1, width), np.linspace(0, height-1, height))
    coords = np.stack((x,y), axis=-1)
    INDX_GRID = coords

    u_grid = distort_coords(coords, cx, cy, k1)

    coords_flat = coords.flatten()  # [0,0,1,0,...,W,0, ... 0,H,1,H,W,H]
    u_grid_flat = u_grid.flatten()

    FACE_AREAS = face_areas
    U_GRID = u_grid
    U_GRID_FLATTEN = u_grid.flatten()

    # manual iteration
    coords_mod = coords_flat
    result_energy_vec = []

    nIter = 100
    alpha_rate_start = 0.01

    for iIter in range(nIter):
        result1_energy = myfunc(coords_mod)
        result1_der = myfunc_der(coords_mod)
        # result_der_debug = map_flat_to_grid(result1_der, height, width)  # debug

        # alpha_rate = alpha_rate_start / (1+ 0.001 * nIter)
        alpha_rate = alpha_rate_start

        coords_mod = coords_mod - alpha_rate * result1_der
        
        #plot current meshgrid
        if (iIter % 5 == 0):
            result_grid = map_flat_to_grid(coords_mod, height, width)
            output_filename = "./results/grid_%d.png" % iIter
            visualize_grid(result_grid, output_filename)
            print("At iteration %d" % iIter)

        result_energy_vec.append(result1_energy)


    """
    # scipy iteration (failed)
    nIter = 10
    tolerance_start = 100
    for iIter in range(nIter):
        tolerance = tolerance_start * (0.6 ** iIter)
        res = minimize(myfunc, U_GRID_FLATTEN, method='BFGS', jac=myfunc_der,
                       options={'gtol': tolerance, 'maxiter': 1, 'disp': True})
        result_grid = map_flat_to_grid(res.x, height, width)
        output_filename = "./results/grid_%d.png" % iIter
        visualize_grid(result_grid, output_filename)
        print("At iteration %d" % iIter)
    """

    # iteration finished
    np.savetxt("./results/result_energy_vec.txt", np.asarray(result_energy_vec))
    print("finished")
