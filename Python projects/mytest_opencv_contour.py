import cv2
import numpy as np

if __name__ == "__main__":

    img_seg = cv2.imread("./test_opencv_contour.png", cv2.IMREAD_COLOR)
    img2 = img_seg.copy()
    img2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)
   # ret, thresh = cv2.threshold(img2, 127, 255, 0)
   # im2, contours, hierarchy = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    im2, contours, hierarchy = cv2.findContours(img2, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    img_draw = np.ones(img_seg.shape,np.uint8)
    cv2.drawContours(img_draw, contours, 3, (0, 255, 0), 3)
    cv2.imwrite("./test_output.png", img_draw)
    print("finished")
