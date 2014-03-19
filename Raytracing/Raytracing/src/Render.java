import java.util.ArrayList;

public class Render extends Thread {

	ArrayList<Sphere> spheres;
	int IMAGE_WIDTH, IMAGE_HEIGHT;
	RGB [] [] image;
	Display display;
	final static int MAX_RAY_DEPTH = 4;
	int begin, end;

	Render(int width, int height, RGB [] [] img, Display dis, ArrayList<Sphere> spheres, int begin, int end) {
		this.IMAGE_WIDTH = width;
		this.IMAGE_HEIGHT = height;
		this.image = img;
		this.display = dis;
		this.spheres = spheres;
		this.begin = begin;
		this.end = end;
	}

	public void run() {

		double invWidth = 1.0 / IMAGE_WIDTH, invHeight = 1.0 / IMAGE_HEIGHT;
		double fov = 30, aspectratio = IMAGE_WIDTH * invHeight ;
		double angle = Math.tan(Math.PI * 0.5 * fov / 180);
		// Trace rays
		for (int y = begin; y < end; ++y) {
			for (int x = 0; x < IMAGE_WIDTH; ++x) {
				double xx = (2 * ((x + 0.5) * invWidth) - 1) * angle * aspectratio;
				double yy = (1 - 2 * ((y + 0.5) * invHeight)) * angle;
				Vec3 raydir = new Vec3(xx, yy, -1.0);
				raydir.normalize();
				image [x] [y] =
						trace(new Vec3(0, 0, 0), raydir, spheres, 0);
			}
			display.repaint() ;
		}


	}

	// This is the main trace function. It takes a ray as argument
	// (defined by its origin and direction). We test if this ray intersects
	// any of the geometry in the scene.
	// If the ray intersects an object, we compute the intersection
	// point, the normal at the intersection point, and shade this point
	// using this information.
	// Shading depends on the surface property (is it transparent,
	// reflective, diffuse).
	// The function returns a color for the ray. If the ray intersects
	// an object that is the color of the object at the intersection point,
	// otherwise it returns the background color.

	public RGB trace(Vec3 rayorig, Vec3 raydir,
			ArrayList<Sphere> spheres, int depth) {

		double tnear = Double.MAX_VALUE;
		Sphere sphere = null;
		// find intersection of this ray with the sphere in the scene
		for (int i = 0; i < spheres.size(); i++) {
			Sphere s = spheres.get(i) ;
			Intersections t = s.intersect(rayorig, raydir) ;
			if (t != null) {
				double t0 = t.t0 < 0 ? t.t1 : t.t0;
				if (t0 < tnear) {
					tnear = t0;
					sphere = s ;
				}
			}
		}
		// if there's no intersection return black or background color
		if (sphere == null) return new RGB(2, 2, 2);
		RGB surfaceColor = new RGB(0, 0, 0) ;
		// color of the ray/surfaceof the object intersected by the ray
		Vec3 phit = new Vec3(rayorig, raydir, tnear) ;
		// point of intersection
		Vec3 nhit = new Vec3(phit, sphere.center, -1.0);
		// normal at the intersection point
		// if the normal and the view direction are not opposite to each other 
		// reverse the normal direction
		if (raydir.dot(nhit) > 0) {
			nhit.x = -nhit.x;
			nhit.y = -nhit.y;
			nhit.z = -nhit.z;
		}
		// normalize normal direction
		nhit.normalize() ;
		double bias = 1e-5;
		// add some bias to the point from which we will be tracing
		if ((sphere.transparency > 0 || sphere.reflection > 0) &&
				depth < MAX_RAY_DEPTH) {
			double IdotN = raydir.dot(nhit); // raydir.normal
			// I and N are not pointing in the same direction, so take
			// the invert
			double facingratio = Math.max(0.0, -IdotN);
			// change the mix value to tweak the effect
			double fresneleffect = mix(Math.pow(1 - facingratio, 3), 1, 0.1);
			// compute reflection direction (not need to normalize because
			// all vectors are already normalized)
			Vec3 refldir = new Vec3(raydir, nhit, -2 * raydir.dot(nhit)) ;
			RGB reflection = trace(new Vec3(phit, nhit, bias),
					refldir, spheres, depth + 1);
			RGB refraction = new RGB(0, 0, 0);
			// if the sphere is also transparent compute refraction
			// ray (transmission)
			if (sphere.transparency != 0) {
				double ior = 1.2, eta = 1 / ior;
				double k = 1 - eta * eta * (1 - IdotN * IdotN);
				Vec3 refrdir = new Vec3(raydir, eta,
						nhit, -(eta * IdotN + Math.sqrt(k)));
				refraction = trace(new Vec3(phit, nhit, -bias), refrdir,
						spheres, depth + 1);
			}
			// the result is a mix of reflection and refraction
			// (if the sphere is transparent)
			double fac = (1 - fresneleffect) * sphere.transparency ;
			surfaceColor.r = (reflection.r * fresneleffect + 
					refraction.r * fac) * sphere.surfaceColor.r;
			surfaceColor.g = (reflection.g * fresneleffect + 
					refraction.g * fac) * sphere.surfaceColor.g;
			surfaceColor.b = (reflection.b * fresneleffect + 
					refraction.b * fac) * sphere.surfaceColor.b;
		}
		else {
			// it's a diffuse object, no need to raytrace any further
			for (int i = 0; i < spheres.size(); i++) {
				Sphere light = spheres.get(i) ;
				if (light.emissionColor != null) {
					// this is a light
					double transmission = 1 ;
					Vec3 lightDirection = new Vec3(light.center, phit, -1.0);
					lightDirection.normalize();
					for (int j = 0; j < spheres.size(); ++j) {
						if (i != j) {
							Sphere sj = spheres.get(j);
							if (sj.intersect(new Vec3(phit, nhit, bias),                                                     lightDirection) != null) {
								transmission = 0;
								break;
							}
						}
					}
					double fac = transmission *
							Math.max(0.0, nhit.dot(lightDirection)) ;
					surfaceColor.r += sphere.surfaceColor.r *
							fac * light.emissionColor.r;
					surfaceColor.g += sphere.surfaceColor.g *
							fac * light.emissionColor.g;
					surfaceColor.b += sphere.surfaceColor.b *
							fac * light.emissionColor.b;
				}
			}
		}
		if(sphere.emissionColor != null) {
			return new RGB(surfaceColor.r + sphere.emissionColor.r,
					surfaceColor.g + sphere.emissionColor.g,
					surfaceColor.b + sphere.emissionColor.b);
		}
		else {
			return surfaceColor ;
		}
	}
	
	static double mix(double a, double b, double mix) {
		return b * mix + a * (1.0 - mix);
	}

}
