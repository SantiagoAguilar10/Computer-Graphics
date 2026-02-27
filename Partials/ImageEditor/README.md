MULTIMEDIA AND COMPUTER GRAPHICS - PARTIAL 1

IMAGE EDITOR

This program works as an image editor allowing the user to edit any png image.

To edit your own image, you must first upload it in the Images Folder, any name will do.
When you execute the program, it will ask you to write the name of the image you want to edit. It is important that you include the extension of the image (.png).

To verify the image was loaded correctly, the user must run the code by executing
Main.java
This will print a message if the image was loaded properly or not.
If the image was loaded successfully, the program will allow the user to choose between the following 5 options:

1.- Invert Colors

    This option allows the user to invert the color of the whole image by modifying every individual pixel using the formula: 255 - (R, G, B)
    Black (0, 0, 0) Becomes White(255, 255, 255). Respectively the RGB colors turn into CMY

2.- Crop

    This option allows the user to crop the current image into a smaller one
    The user must input 2(x, y) coordinates to measure a quadrilateral (Square or Rectangle) whose area will become the new Image. 
    All pixels outside the range defined by the quadrilateral are not deleted, but lost. 
    As the crop actually functions like creating a new image.

3.- Rotate

    This option allows the user to define a quadrilateral segment using two x and y coordinates. To rotate 90, 180, or 270 degrees.
    The function worls properly for any quadrilateral given/defined by the user.
    All rectangular areas thet are rotated 90 or 270 will leave a black/void area in the place they were previously at.
    It is important to remember that if the quadrilateral segment (when rotated) exceeds the image's dimensions. The whole image will not be resized for the segment to fit, but the quadrilateral segment that exceeds the image borders will not be drawn.

4.- Save Image

    This option allows the user to save it's edited image (no matter how many process were previously done to it) in the Outputs Folder. It also allows the user to name the output file (image). The user must include the extension of the file when naming it. (PNG should be the most optimal option).

5.- Exit

    This option puts an end to the program. Both the input and output (edited) image will remain in their respective folders.

For any following questions, feel free to review the code and it's comments, as they provide more technical information about the methods, functionality, validation, and structure of the program.