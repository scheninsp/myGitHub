import numpy as np
from scipy.optimize import minimize
import copy

WIDTH=114
HEIGHT=86

FACE_AREAS = np.zeros((2,4),dtype = np.float32)
INDX_GRID = np.zeros ( (HEIGHT, WIDTH, 2), dtype = np.float32)  # indicate original index
U_GRID = np.zeros ( (HEIGHT, WIDTH, 2), dtype = np.float32)  # undistort grid using radial function
U_GRID_FLATTEN = U_GRID.flatten()

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
    face_areas_indx_x = [[]]
    for k in range(kFace-1):
        face_areas_indx_x.append([])
    face_areas_indx_y = copy.deepcopy(face_areas_indx_x)
    for k in range(kFace):
        for i in range(FACE_AREAS[k, 2], FACE_AREAS[k, 3]):
            for j in range (FACE_AREAS[k, 0], FACE_AREAS[k, 1]):
                face_areas_indx_x[k].append(2 * WIDTH * i + 2 * j)
                face_areas_indx_y[k].append(2 * WIDTH * i + 2 * j + 1)

    energy = 0
    for k in range(kFace):
        energy += np.sum((coords[face_areas_indx_x[k]] - U_GRID_FLATTEN[face_areas_indx_x[k]]) ** 2)

    return energy
"""
def myfunc_der(coords):
    return coords_der_out
"""
if __name__ == "__main__":

    width = WIDTH
    height = HEIGHT

    cx = width/2
    cy = height/2
    k_scale = 1 / (cx**2 + cy**2)
    k1 = k_scale * 1.4

    face_areas = np.asarray([[10,20,10,20], [80,100,60,80]])

    x,y = np.meshgrid(np.linspace(0, width-1, width), np.linspace(0, height-1, height))
    coords = np.stack((x,y), axis=-1)
    INDX_GRID = coords

    u_grid = distort_coords(coords, cx, cy, k1)

    coords_flat = coords.flatten()  # [0,0,1,0,...,W,0, ... 0,H,1,H,W,H]
    u_grid_flat = u_grid.flatten()

    FACE_AREAS = face_areas
    U_GRID = u_grid
    U_GRID_FLATTEN = u_grid.flatten()

    result1 = myfunc(coords_flat)

    print("finished")