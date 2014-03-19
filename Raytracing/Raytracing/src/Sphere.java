
public class Sphere {

	Vec3 center ;             // position of the sphere
	double radius, radius2;   // sphere radius and radius^2

	RGB surfaceColor, emissionColor;  // surface color and emission (light)

	double transparency, reflection;  // surface transparency and reflectivity

	Sphere(Vec3 center,
			double radius, RGB surfaceColor, double reflection,
			double transparency, RGB emissionColor) {
		this.center = center ;
		this.radius = radius ;
		radius2 = radius * radius ;
		this.surfaceColor = surfaceColor ;
		this.emissionColor = emissionColor ;
		this.transparency = transparency;
		this.reflection = reflection ;
	}

	Sphere(Vec3 center,
			double radius, RGB surfaceColor, double reflection,
			double transparency) {
		this(center, radius, surfaceColor, reflection, transparency, null) ;
	}

	// compute a ray-sphere intersection using the geometric solution
	Intersections intersect(Vec3 rayorig, Vec3 raydir) {

		Vec3 l = new Vec3(center, rayorig, -1.0) ;
		double tca = l.dot(raydir) ;
		if (tca < 0) return null;
		double d2 = l.dot(l) - tca * tca;
		if (d2 > radius2) return null;
		double thc = Math.sqrt(radius2 - d2);

		return new Intersections(tca - thc, tca + thc) ;
	}
}
