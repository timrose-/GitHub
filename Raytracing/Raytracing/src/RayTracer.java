
/*
 * Simple Java Raytracer adapted from C++ code at:
 *
 *   http://www.scratchapixel.com/lessons/3d-basic-lessons/lesson-1-writing-a-simple-raytracer/
 *
 * which included the copyright notice below.
 */

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// A very basic raytracer example.
//
// Compile with the following command: c++ -o raytracer -O3 -Wall raytracer.cpp
//
// Copyright (c) 2011 Scratchapixel, all rights reserved
//
// * To report a bug, contact webmaster@scratchapixel.com
// * Redistribution and use in source and binary forms, with or without modification, are permitted
// * You use this software at your own risk and are responsible for any damage you cause by using it
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class RayTracer {

	//final static int IMAGE_WIDTH = 640, IMAGE_HEIGHT = 480;

	final static int IMAGE_WIDTH = 1280, IMAGE_HEIGHT = 960;

	final static int MAX_RAY_DEPTH = 4 ;

	static int numThreads;
	static int blockSize;

	static RGB [] [] image = new RGB [IMAGE_WIDTH] [IMAGE_HEIGHT] ;

	public static void main(String [] args) throws InterruptedException {

		ArrayList<Sphere> spheres = new ArrayList<Sphere>() ;

		// Set up scene...

		// position, radius, surface color, reflectivity, transparency, emission color
		spheres.add(new Sphere(new Vec3(0, -10004, -20), 10000,
				new RGB(0.2, 0.2, 0.2), 0, 0.0));
		spheres.add(new Sphere(new Vec3(0, 0, -20), 4,
				new RGB(1.00, 0.32, 0.36), 1, 0.5));
		spheres.add(new Sphere(new Vec3(5, -1, -15), 2,
				new RGB(0.90, 0.76, 0.46), 1, 0.0));
		spheres.add(new Sphere(new Vec3(5, 0, -25), 3,
				new RGB(0.65, 0.77, 0.97), 1, 0.0));
		spheres.add(new Sphere(new Vec3(-5.5, 0, -15), 3,
				new RGB(0.90, 0.90, 0.90), 1, 0.0));

		// light
		spheres.add(new Sphere(new Vec3(0, 20, -30), 3,
				new RGB(0, 0, 0), 0, 0, new RGB(3, 3, 3)));

		System.out.println("Enter the number of threads to run?");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			numThreads = Integer.parseInt(br.readLine());
		} catch (IOException ioe) {
		}
		
		blockSize = IMAGE_HEIGHT/numThreads;
		
		Display display = new Display(IMAGE_WIDTH, IMAGE_HEIGHT, image) ;
		
		double startTime = System.currentTimeMillis();
		
        Render [] threads = new Render [numThreads];
		for (int i=0; i<numThreads; i++) {
			threads[i] = new Render(IMAGE_WIDTH, IMAGE_HEIGHT, image, display, spheres, i*blockSize, (i+1)*blockSize);
			threads[i].start();
		}
		
		for (int i=0; i<numThreads; i++) {
			threads[i].join();
		}

//		Render thread1 = new Render(IMAGE_WIDTH, IMAGE_HEIGHT, image, display, spheres, 0, IMAGE_HEIGHT/2);
//		Render thread2 = new Render(IMAGE_WIDTH, IMAGE_HEIGHT, image, display, spheres, IMAGE_HEIGHT/2, IMAGE_HEIGHT);
//
//		thread1.start();
//		thread2.start();
//
//		thread1.join();
//		thread2.join();

		double endTime = System.currentTimeMillis();

		double timeTaken = endTime - startTime;

		System.out.println("Calculated in " +
				(long)timeTaken + " milliseconds");

	}

}