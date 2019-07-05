import numpy as np
from scipy.optimize import minimize
import copy
import cv2
from math import sqrt
import time

WIDTH=114
HEIGHT=86

# neighbour for one pixel
DW = 4
DH = 4

FACE_AREAS = np.zeros((2,4),dtype = np.float32)

LAMBDA_F = 4
LAMBDA_B = 0.5
LAMBDA_R = 0.1

ADD_CONSTRAIN_BOUNDARY_ORI = True
NPAD = 4

WIDTH_PAD = WIDTH + 2*NPAD
HEIGHT_PAD = HEIGHT +2*NPAD

INDX_GRID = np.zeros ( (HEIGHT_PAD, WIDTH_PAD, 2), dtype = np.float32)  # indicate original index
INDX_GRID_FLATTEN = INDX_GRID.flatten()
U_GRID = np.zeros ( (HEIGHT_PAD, WIDTH_PAD, 2), dtype = np.float32)  # undistort grid using radial function
U_GRID_FLATTEN = U_GRID.flatten()
EIJ_GRID = np.zeros ( (HEIGHT_PAD, WIDTH_PAD, 2*DH+1, 2*DW+1, 2), dtype = np.float32)
# eij from point i to point j, (piy, pix, pjy, pjx , 2)


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
                face_areas_indx_x[k].append(2 * WIDTH_PAD * i + 2 * j)
                face_areas_indx_y[k].append(2 * WIDTH_PAD * i + 2 * j + 1)

    ef = 0
    for k in range(kFace):
        ef += np.sum((coords[face_areas_indx_x[k]] - U_GRID_FLATTEN[face_areas_indx_x[k]]) ** 2)

    # term of || vi - vj ||^2
    er = 0
    for i in range(HEIGHT_PAD):
        for j in range(WIDTH_PAD):
            tmpx = 0
            tmpy = 0
            neighbour_area_indx_x = []
            neighbour_area_indx_y = []
            for i2 in range(max(0, i-DH),min(HEIGHT_PAD,i+DH+1)):
                for j2 in range(max(0, j-DW),min(WIDTH_PAD,j+DW+1)):
                    neighbour_area_indx_x.append(2 * WIDTH_PAD * i2 + 2 * j2)
                    neighbour_area_indx_y.append(2 * WIDTH_PAD * i2 + 2 * j2 + 1)

            lx = len(neighbour_area_indx_x)
            ly = len(neighbour_area_indx_y)
            tmpx += np.sum((coords[2 * WIDTH_PAD * i + 2 * j] * np.ones(lx, dtype=np.float32) -
                     coords[neighbour_area_indx_x]) ** 2)
            tmpy += np.sum((coords[2 * WIDTH_PAD * i + 2 * j + 1] * np.ones(ly, dtype=np.float32) -
                     coords[neighbour_area_indx_y]) ** 2)

            er += (tmpx + tmpy)

    # term of || (vi - vj) x eij ||^2
    eb = 0
    for i in range(HEIGHT_PAD):
        for j in range(WIDTH_PAD):
            neighbour_area_indx_x = []
            neighbour_area_indx_y = []
            for i2 in range(max(0, i - DH), min(HEIGHT_PAD, i + DH + 1)):
                for j2 in range(max(0, j - DW), min(WIDTH_PAD, j + DW + 1)):
                    neighbour_area_indx_x.append(2 * WIDTH_PAD * i2 + 2 * j2)
                    neighbour_area_indx_y.append(2 * WIDTH_PAD * i2 + 2 * j2 + 1)

            lx = len(neighbour_area_indx_x)
            ly = len(neighbour_area_indx_y)
            actual_dh = min(HEIGHT_PAD, i + DH + 1) - max(0, i - DH)   #for image boundary regions
            actual_dw = min(WIDTH_PAD, j + DW + 1) - max(0, j - DW)

            #reshape EIJ_GRID into same shape with coords[neighbour_area_indx]
            eij_neighbour_flat =  (EIJ_GRID[i,j,0:actual_dh,0:actual_dw,:]).flatten()
            len_e = len(eij_neighbour_flat)

            #if len_e != (lx+ly):
            #    print("debug!")

            # (xi - xj) * y_eij - x_eij * (yi-yj) , cross-product
            tmp1 = (coords[2 * WIDTH_PAD * i + 2 * j] * np.ones(lx, dtype=np.float32) -
                            coords[neighbour_area_indx_x]) * eij_neighbour_flat[list(range(1,len_e,2))]
            tmp2 = (coords[2 * WIDTH_PAD * i + 2 * j + 1] * np.ones(ly, dtype=np.float32) -
                            coords[neighbour_area_indx_y]) * eij_neighbour_flat[list(range(0,len_e,2))]

            # print("debug")

            eb += np.sum((tmp1 - tmp2) ** 2)

    energy = LAMBDA_F * ef + LAMBDA_R * er +LAMBDA_B * eb

    return energy

def myfunc_constrain(coords):
    constrain = 0
    indx_top = np.asarray(list(range(0,2*WIDTH_PAD))) + 2*WIDTH_PAD *0
    indx_bottom = np.asarray(list(range(0, 2*WIDTH_PAD))) + 2*WIDTH_PAD *(HEIGHT_PAD - 1)
    tmp1 =  np.asarray(list(range(0, 2*WIDTH_PAD*HEIGHT_PAD, 2*WIDTH_PAD))) + 0 # x
    tmp2 =  np.asarray(list(range(0, 2*WIDTH_PAD*HEIGHT_PAD, 2*WIDTH_PAD))) + 1 # y
    tmp1.append(tmp2)
    indx_left = np.asarray(tmp1)
    
    tmp1 =  np.asarray(list(range(0, 2*WIDTH_PAD*HEIGHT_PAD, 2*WIDTH_PAD))) + (2*WIDTH_PAD-2) # x
    tmp2 =  np.asarray(list(range(0, 2*WIDTH_PAD*HEIGHT_PAD, 2*WIDTH_PAD))) + (2*WIDTH_PAD-1) # y
    tmp1.append(tmp2)
    indx_right = np.asarray(tmp1)

    constrain += coords[indx_top] - INDX_GRID_FLATTEN[indx_top]
    constrain += coords[indx_bottom] - INDX_GRID_FLATTEN[indx_bottom]
    constrain += coords[indx_left] - INDX_GRID_FLATTEN[indx_left]
    constrain += coords[indx_right] - INDX_GRID_FLATTEN[indx_right]

    return constrain

    
def myfunc_der(coords):
    # - coords 1*(2*W*H)
    # - face_areas : nFace * 4(xmin, xmax, ymin, ymax)
    
    if(len(coords) % 2 != 0):
        print("ERROR: length of coords must be even")
        return coords
        
    nvar = int(len(coords)/2)

    # derivative of ef
    ef_der = np.zeros(2*nvar, dtype=np.float32)
    kFace = FACE_AREAS.shape[0]
    
    face_areas_indx = [[]]
    for k in range(kFace -1):
        face_areas_indx.append([])
    
    for k in range(kFace):
        for i in range(FACE_AREAS[k, 2], FACE_AREAS[k, 3]):
            for j in range(FACE_AREAS[k,0], FACE_AREAS[k,1]):
                face_areas_indx[k].append(2 * WIDTH_PAD * i + 2 * j)
                face_areas_indx[k].append(2 * WIDTH_PAD * i + 2 * j + 1)

    for k in range(kFace):
        ef_der[face_areas_indx[k]] = ef_der[face_areas_indx[k]] + \
        2 * (coords[face_areas_indx[k]] - U_GRID_FLATTEN[face_areas_indx[k]])

    #derivative of er
    er_der = np.zeros(2*nvar, dtype=np.float32)
    
    for i in range(HEIGHT_PAD):
        for j in range(WIDTH_PAD):
            
            tmpx=0
            tmpy=0
            neighbour_area_indx_x = []
            neighbour_area_indx_y = []
            
            for i2 in range(max(0, i-DH), min(HEIGHT_PAD, i+DH+1)):
                for j2 in range(max(0, j-DW), min(WIDTH_PAD, j+DW+1)):
                    neighbour_area_indx_x.append(2*WIDTH_PAD*i2+2*j2)
                    neighbour_area_indx_y.append(2*WIDTH_PAD*i2 + 2*j2 + 1)
            
            lx = len(neighbour_area_indx_x)
            ly = len(neighbour_area_indx_y)
            er_der[2*WIDTH_PAD*i + 2*j] = 4 * np.sum(coords[2*WIDTH_PAD*i + 2*j] * np.ones(lx, dtype=np.float32) - coords[neighbour_area_indx_x])
            er_der[2*WIDTH_PAD*i + 2*j + 1] = 4 * np.sum(coords[2*WIDTH_PAD*i + 2*j + 1] * np.ones(lx, dtype=np.float32) - coords[neighbour_area_indx_y])

    # derivative of eb
    eb_der = np.zeros(2*nvar, dtype=np.float32)
    for i in range(HEIGHT_PAD):
        for j in range(WIDTH_PAD):
            neighbour_area_indx_x = []
            neighbour_area_indx_y = []
            for i2 in range(max(0, i - DH), min(HEIGHT_PAD, i + DH + 1)):
                for j2 in range(max(0, j - DW), min(WIDTH_PAD, j + DW + 1)):
                    neighbour_area_indx_x.append(2 * WIDTH_PAD * i2 + 2 * j2)
                    neighbour_area_indx_y.append(2 * WIDTH_PAD * i2 + 2 * j2 + 1)

            lx = len(neighbour_area_indx_x)
            ly = len(neighbour_area_indx_y)
            actual_dh = min(HEIGHT_PAD, i + DH + 1) - max(0, i - DH)   #for image boundary regions
            actual_dw = min(WIDTH_PAD, j + DW + 1) - max(0, j - DW)

            #reshape EIJ_GRID into same shape with coords[neighbour_area_indx]
            eij_neighbour_flat =  (EIJ_GRID[i,j,0:actual_dh,0:actual_dw,:]).flatten()
            len_e = len(eij_neighbour_flat)

            #if len_e != (lx+ly):
            #    print("debug!")

            # (xi - xj) * y_eij - x_eij * (yi-yj) , cross-product
            tmp1 = (coords[2 * WIDTH_PAD * i + 2 * j] * np.ones(lx, dtype=np.float32) -
                            coords[neighbour_area_indx_x]) * eij_neighbour_flat[list(range(1,len_e,2))]
            tmp2 = (coords[2 * WIDTH_PAD * i + 2 * j + 1] * np.ones(ly, dtype=np.float32) -
                            coords[neighbour_area_indx_y]) * eij_neighbour_flat[list(range(0,len_e,2))]

            eb_der[2*WIDTH_PAD*i + 2*j] = 4 * np.sum((tmp1 - tmp2) * eij_neighbour_flat[list(range(1,len_e,2))])
            eb_der[2*WIDTH_PAD*i + 2*j + 1] = 4 * np.sum((tmp1 - tmp2) *  (-eij_neighbour_flat[list(range(0, len_e, 2))]) )

    e_der = LAMBDA_F * ef_der + LAMBDA_R * er_der + LAMBDA_B * eb_der
    
    if ADD_CONSTRAIN_BOUNDARY_ORI:
        indx_top = np.asarray(list(range(0,2*WIDTH_PAD))) + 2*WIDTH_PAD *0
        indx_bottom = np.asarray(list(range(0, 2*WIDTH_PAD))) + 2*WIDTH_PAD *(HEIGHT_PAD - 1)
        tmp1 =  np.asarray(list(range(0, 2*WIDTH_PAD*HEIGHT_PAD, 2*WIDTH_PAD))) + 0 # x
        tmp2 =  np.asarray(list(range(0, 2*WIDTH_PAD*HEIGHT_PAD, 2*WIDTH_PAD))) + 1 # y
        indx_left = np.concatenate((tmp1,tmp2),axis=0)

        tmp1 =  np.asarray(list(range(0, 2*WIDTH_PAD*HEIGHT_PAD, 2*WIDTH_PAD))) + (2*WIDTH_PAD-2) # x
        tmp2 =  np.asarray(list(range(0, 2*WIDTH_PAD*HEIGHT_PAD, 2*WIDTH_PAD))) + (2*WIDTH_PAD-1) # y
        indx_right = np.concatenate((tmp1,tmp2),axis=0)

        e_der[indx_left] = 0
        e_der[indx_right] = 0
        e_der[indx_top] = 0
        e_der[indx_bottom] = 0
    
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
    new_height = (height+1) * pixel_inter
    new_width = (width+1) * pixel_inter
    img_draw = 255 * np.ones((new_height, new_width, 3),np.uint8)

    #expand meshgrid
    coords_grid = coords_grid * pixel_inter
    coords_grid_mask = np.all ((coords_grid >= [0,0]) & (coords_grid < [new_width, new_height]), axis=-1)

    color = (255,0,0)
    thick = 2
    for i in range(height):
        for j in range(width-1):
            if coords_grid_mask[i,j] and coords_grid_mask[i,j+1]:
                cv2.line(img_draw, tuple(coords_grid[i,j,:]+pixel_inter),tuple(coords_grid[i,j+1,:]+pixel_inter),
                     color, thick)

    for j in range(width):
        for i in range(height-1):
            if coords_grid_mask[i,j] and coords_grid_mask[i+1,j]:
                cv2.line(img_draw, tuple(coords_grid[i,j,:]+pixel_inter),tuple(coords_grid[i+1,j,:]+pixel_inter),
                     color, thick)

    cv2.imwrite(output_filename,img_draw)

if __name__ == "__main__":

    cx = WIDTH/2
    cy = HEIGHT/2
    k_scale = 1 / (cx**2 + cy**2)
    k1 = k_scale * 1.4

    face_areas = np.asarray([[10,30,10,40], [80,100,20,50]])

    x,y = np.meshgrid(np.linspace(0, WIDTH_PAD-1, WIDTH_PAD), np.linspace(0, HEIGHT_PAD-1, HEIGHT_PAD))
    coords = np.stack((x,y), axis=-1)
    INDX_GRID = coords
    INDX_GRID_FLATTEN = coords.flatten()

    for i in range(HEIGHT_PAD):
        for j in range(WIDTH_PAD):
            ishift = 0
            for i2 in range(max(0, i - DH), min(HEIGHT_PAD, i + DH + 1)):
                jshift = 0
                for j2 in range(max(0, j - DW), min(WIDTH_PAD, j + DW + 1)):
                    # ishift = i2 - i + DH
                    # jshift = j2 - j + DW   # do not use this for consistency within myfunc() eb
                    # do not use this to ensure for boundary regions, indices are in the up-left corner in E[i,j,:]
                    EIJ_GRID[i,j,ishift,jshift,:] = INDX_GRID[i,j,:] - INDX_GRID[i2,j2,:]
                    norm = sqrt(np.sum(EIJ_GRID[i,j,ishift,jshift,:] ** 2))
                    if norm != 0 :
                        EIJ_GRID[i, j, ishift, jshift, :] = EIJ_GRID[i,j,ishift,jshift,:]/norm
                    jshift += 1
                ishift += 1

    u_grid = distort_coords(coords, cx+NPAD, cy+NPAD, k1)

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
        # result_der_debug = map_flat_to_grid(result1_der, HEIGHT_PAD, WIDTH_PAD)  # debug

        # alpha_rate = alpha_rate_start / (1+ 0.001 * nIter)
        alpha_rate = alpha_rate_start

        coords_mod = coords_mod - alpha_rate * result1_der
        
        #plot current meshgrid
        if (iIter % 5 == 0):
            result_grid = map_flat_to_grid(coords_mod, HEIGHT_PAD, WIDTH_PAD)
            output_filename = "./results/grid_%d.png" % iIter

            # result_grid_crop = result_grid[NPAD:NPAD+HEIGHT-1,NPAD:NPAD+WIDTH-1,:]
            # visualize_grid(result_grid_crop, output_filename)
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
