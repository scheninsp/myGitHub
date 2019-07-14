# separate pictures into N parts on width
import cv2
import copy
import numpy as np

NPART = 2

if __name__ == "__main__":

    filename = "./eve_b1.jpg"
    img = cv2.imread(filename, cv2.IMREAD_COLOR)
    height, width = img.shape[:2]

    a4_ratio = 29.7/21
    a4_width = int(1280*0.78)  # 0.78 for insert picture to full page in word
    a4_height = int(a4_width / a4_ratio)

    npart = NPART
    sep_width = int(width/npart)
    sep_height = int(sep_width/a4_ratio)
    nheight = int(height/sep_height)
    # rem_width = width - npart * sep_width  #this is small (<npart) so need no process
    rem_height = height - nheight * sep_height

    cnt = 0
    for ipart in range(npart):
        for jheight in range(nheight):
            new_img = copy.deepcopy(img[(jheight*sep_height):((jheight+1)*sep_height),
                                        (ipart*sep_width):((ipart+1)*sep_width),:])
            new_img = cv2.resize(new_img, (a4_width, a4_height))
            output_filename = "./sep%d.png" % cnt
            cv2.imwrite(output_filename, new_img)
            cnt += 1

    if rem_height != 0:
        for ipart in range(npart):
            new_img = copy.deepcopy(img[(nheight*sep_height):,
                                        (ipart*sep_width):((ipart+1)*sep_width), :])
            extend_height_img = 255 * np.ones((sep_height - rem_height, sep_width, 3),dtype=np.uint8)
            new_img = np.concatenate((new_img, extend_height_img), axis=0)
            new_img = cv2.resize(new_img, (a4_width, a4_height))
            output_filename = "./sep%d.png" % cnt
            cv2.imwrite(output_filename, new_img)
            cnt += 1

