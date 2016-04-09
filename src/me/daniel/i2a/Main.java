package me.daniel.i2a;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import javax.imageio.ImageIO;

public class Main
{

	private int[] pixels;
	private int width, height;
	private static TreeMap<Integer, Character> chars = new TreeMap<>();
	static {
		chars.put(  0, ' ');
		chars.put(  1, '.');
		chars.put(  5, ',');
		chars.put( 10, ':');
		chars.put( 20, ';');
		chars.put( 30, '"');
		chars.put( 40, '\\');
		chars.put( 50, '[');
		chars.put( 60, '{');
		chars.put( 70, '=');
		chars.put( 80, '?');
		chars.put( 90, '~');
		chars.put(100, '+');
		chars.put(150, '&');
		chars.put(200, '#');
		chars.put(255, '@');
	}

	public static void main(String[] args) {
		new Main();
	}

	public Main()
	{
		System.out.println("i2a: Image to ASCII - Daniel Shaw, April 9th, 2016");
		System.out.print("Please enter the path to an image: ");
		Scanner scanner = new Scanner(System.in);
		File image = new File(scanner.nextLine().replace("\"", "").trim());
		if(!image.exists()) {
			System.err.println("File does not exist.");
			System.exit(-1);
		}
		try {
			decompileImage(ImageIO.read(image));
		} catch (IOException e) {
			System.err.println("Could not read image.");
			System.exit(-2);
		}
		System.out.print("Please enter the path to save this image to: ");
		File out = new File(scanner.nextLine().replace("\"", "").trim());
		if(out.exists()) {
			loop: while(true) {
				System.out.print("A file with this name already exists. Overwrite? (y/n): ");
				if("y".equalsIgnoreCase(scanner.nextLine().trim())) {
					break;
				} else {
					System.out.print("Please enter a new path: ");
					File temp = new File(scanner.nextLine().replace("\"", "").trim());
					if(temp.equals(out) || temp.exists()) {
						System.out.println("This file already exists.");
						continue loop;
					} else {
						out = temp;
						break;
					}	
				}
			}
		}
		scanner.close();
		writeOutput(out);
		return;
	}
	
	private void writeOutput(File out) {
		List<String> lines = new ArrayList<>();
		String currentLine;
		for(int y = 0; y < height; y++) {
			currentLine = "";
			for(int x = 0; x < width; x++) {
				int hex = pixels[y * width + x];
				int a = (hex & 0xFF000000) >> 24;
				if(a==0) {
					currentLine += ' ';
					continue;
				}
				int r = (hex & 0xFF0000) >> 16;
				int g = (hex & 0xFF00) >> 8;
				int b = (hex & 0xFF);
				int avg = 255 - (r + g + b)/3;
				currentLine += chars.floorEntry(avg).getValue();
			}
			lines.add(currentLine);
		}
		try(PrintStream fw = new PrintStream(out)) {
			for(int i = 0; i < lines.size()-1; i++) {
				fw.println(lines.get(i));
				fw.flush();
			}
		} catch(IOException e) {
			System.err.println("There was an error writing to the file.");
			System.exit(-4);
		}
		System.out.println("Wrote output to " + out.getAbsolutePath());
	}

	private void decompileImage(BufferedImage img) {
		PixelGrabber grabber = new PixelGrabber(img, 0, 0, -1, -1, true);
		try {
			if(grabber.grabPixels()) {
				width = grabber.getWidth();
				height = grabber.getHeight();
				pixels = (int[]) grabber.getPixels();
			}
		} catch (InterruptedException e) {
			System.err.println("There was a problem reading this image.");
			System.exit(-3);
		}
	}
}