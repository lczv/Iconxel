fun main(args: Array<String>) {

    /* This is a small utility that recreates a source image, using an array of smaller images*/

    val sourceImagePath = "test/avatar.jpg" // what image should be "converted"?
    val outputImagePath = "test/output.png" // where the converted image should be saved?
    val outputTextFilePath = "test/output_text.txt" // where the output text should be saved?
    val iconsFolderPath = "test/icons" // Folder containing the source icons
    val iconSize = 18 // Size in which the icons will be resized

    /* For now, only square images are supported
    *  The final image size will not be in pixels, but rather iconSize*outPutImageSize*/
    val outputImageSize = 32

    ImageConverter.process(sourceImagePath, iconsFolderPath, outputImagePath, outputTextFilePath, iconSize, outputImageSize)
}