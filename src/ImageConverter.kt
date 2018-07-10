import java.awt.Color
import java.awt.image.BufferedImage
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.imageio.ImageIO

class ImageConverter {

    companion object {

        private var sourceImage: BufferedImage? = null
        private var resizedImage: BufferedImage? = null
        private var outputImage: BufferedImage? = null
        private var iconsMap = mutableMapOf<String, Triple<Int, Int, Int>>()

        fun convert(resizedImage: BufferedImage, iconsMap: Map<String, Triple<Int, Int, Int>>, outputImagePath: String, outputFileWriter: BufferedWriter, iconSize: Int, outputImageSize: Int) {

            var completion = resizedImage.width * resizedImage.height
            var cntStep = 0

            outputImage = BufferedImage(resizedImage.width * iconSize, resizedImage.height * iconSize, BufferedImage.TYPE_INT_ARGB)

            for (y in 0..resizedImage.width - 1) {
                for (x in 0..resizedImage.height - 1) {
                    var pixelColorTriple = Triple(Color(resizedImage.getRGB(x, y)).red,
                            Color(resizedImage.getRGB(x, y)).green,
                            Color(resizedImage.getRGB(x, y)).blue)

                    var nearestImagePath = ""
                    var nearestImageValue = Integer.MAX_VALUE

                    // Iterate through all icons mean values
                    iconsMap.forEach { key, value ->
                        val distance = calculateDistance(pixelColorTriple, value)
                        if (distance < nearestImageValue) {
                            nearestImageValue = distance
                            nearestImagePath = key
                        }
                        //println("Pixel ($x,$y) RGB (${pixelColorTripe.first},${pixelColorTripe.second},${pixelColorTripe.third}) -> Icon $key Distance $distance")
                    }

                    // Opens again the nearest image found
                    var tempIcon = ImageIO.read(File(nearestImagePath))
                    var resizedTempIcon = BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB)

                    var g = resizedTempIcon.createGraphics()
                    g.drawImage(tempIcon, 0, 0, iconSize, iconSize, null)
                    g.dispose()

                    // Copy the pixels of the nearest image to the output image
                    for (xIcon in 0..resizedTempIcon.width - 1) {
                        for (yIcon in 0..resizedTempIcon.height - 1) {
                            outputImage!!.setRGB((x * iconSize + xIcon), (y * iconSize + yIcon)
                                    , resizedTempIcon.getRGB(xIcon, yIcon))
                        }
                    }

                    outputFileWriter.write(nearestImagePath)

                    cntStep++
                    println("Completion: ${(((cntStep) * 100) / completion.toFloat()).toInt()}%")

                    //println("Nearest ($x,$y) $nearestImagePath Distance-> $nearestImageValue")

                }
                outputFileWriter.write(System.lineSeparator())
            }
            ImageIO.write(outputImage, "png", File(outputImagePath))
            outputFileWriter.close()
        }

        fun process(sourceImagePath: String, iconsFolderPath: String, outputImagePath: String, outputTextFilePath: String, iconSize: Int, outputImageSize: Int) {

            println("Reading source images...")

            // Load the source image
            try {
                sourceImage = ImageIO.read(File(sourceImagePath))
                resizedImage = BufferedImage(outputImageSize, outputImageSize, BufferedImage.TYPE_INT_ARGB);

                val g = resizedImage!!.createGraphics();
                g.drawImage(sourceImage, 0, 0, outputImageSize, outputImageSize, null);
                g.dispose();

            } catch (exception: IOException) {
                exception.printStackTrace()
                println("Error while reading source image")
            }

            // Load the icons
            try {
                // Iterate through all icons in folder
                val icons = File(iconsFolderPath).listFiles()
                icons.forEach {
                    iconsMap.put(it.absoluteFile.toString(),
                            calculateMeanPixelValues(ImageIO.read(File(it.absoluteFile.toString()))))

                }


            } catch (exception: IOException) {
                exception.printStackTrace()
                println("Error while reading icons folder")
            }

            // Open the output text file
            var outputFileWriter: BufferedWriter? = null
            try {
                outputFileWriter = BufferedWriter(FileWriter(outputTextFilePath));
            } catch (exception: IOException) {
                exception.printStackTrace()
                println("Error while trying to save text file")
            }

            println("${iconsMap.size} images found...")
            convert(resizedImage!!, iconsMap, outputImagePath, outputFileWriter!!, iconSize, outputImageSize)


            /*iconsMap.forEach { key, value ->
                print("${key} ${value}\n")
            }*/

        }

        // Calculates the euclidean distance between the RGB values of two given pixels
        fun calculateDistance(firstRgb: Triple<Int, Int, Int>, secondRgb: Triple<Int, Int, Int>): Int {

            val distance = Math.sqrt(Math.pow(firstRgb.first.toDouble() - secondRgb.first.toDouble(), 2.0)
                    + Math.pow(firstRgb.second.toDouble() - secondRgb.second.toDouble(), 2.0)
                    + Math.pow(firstRgb.third.toDouble() - secondRgb.third.toDouble(), 2.0))

            return distance.toInt()
        }

        // Calculate the mean value of all the pixels of the given image
        fun calculateMeanPixelValues(image: BufferedImage): Triple<Int, Int, Int> {
            var meanRed = 0
            var meanGreen = 0
            var meanBlue = 0

            for (x in 0..image.width - 1) {
                for (y in 0..image.height - 1) {
                    val color = Color(image.getRGB(x, y))
                    meanRed += color.red
                    meanGreen += color.green
                    meanBlue += color.blue
                }
            }

            meanRed /= (image.width * image.height)
            meanGreen /= (image.width * image.height)
            meanBlue /= (image.width * image.height)

            //println("Mean ${meanRed},${meanGreen},${meanBlue}")
            return Triple(meanRed, meanGreen, meanBlue)
        }

    }

}