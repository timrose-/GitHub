
public class Vec3 {

		double x, y, z;

		Vec3(double x, double y, double z) {
			this.x = x ;
			this.y = y ;
			this.z = z ;
		}

		Vec3(Vec3 u, Vec3 v, double fac) {
			this.x = u.x + fac * v.x ;
			this.y = u.y + fac * v.y ;
			this.z = u.z + fac * v.z ;
		}

		Vec3(Vec3 u, double fac1, Vec3 v, double fac2) {
			this.x = fac1 * u.x + fac2 * v.x ;
			this.y = fac1 * u.y + fac2 * v.y ;
			this.z = fac1 * u.z + fac2 * v.z ;
		}

		void normalize()
		{
			double nor = x * x + y * y + z * z;
			if (nor > 0) {
				double invNor = 1 / Math.sqrt(nor);
				x *= invNor ;
				y *= invNor ;
				z *= invNor ;
			}
		}

		double dot(Vec3 v) {
			return x * v.x + y * v.y + z * v.z;
		}
	}