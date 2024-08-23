/****************************************************************
 *               Distributions_2F1.  I believe from Distr.      *
 *                     It is here b/c it has 2F1                *
 *               Combine with other Distributions Soon          *
 ***************************************************************/

package probabilityDistributions;

public class Distributions_2F1 {
    
	public static double Negate(double x){
		return -x;
	}

	public static double Positive(double x){
		return +x;
	}

	public static double Factorial(double x){
		double i, f;

		f = 1d;

		for(i = 2d; i <= x; i = i + 1d){
			f = f*i;
		}

		return f;
	}

	public static double Round(double x){
		return Math.floor(x + 0.5);
	}

	public static double BankersRound(double x){
		double r;

		if(Absolute(x - Truncate(x)) == 0.5){
			if(!DivisibleBy(Round(x), 2d)){
				r = Round(x) - 1d;
			}else{
				r = Round(x);
			}
		}else{
			r = Round(x);
		}

		return r;
	}

	public static double Ceil(double x){
		return Math.ceil(x);
	}

	public static double Floor(double x){
		return Math.floor(x);
	}

	public static double Truncate(double x){
		double t;

		if(x >= 0d){
			t = Math.floor(x);
		}else{
			t = Math.ceil(x);
		}

		return t;
	}

	public static double Absolute(double x){
		return Math.abs(x);
	}

	public static double Logarithm(double x){
		return Math.log10(x);
	}

	public static double NaturalLogarithm(double x){
		return Math.log(x);
	}

	public static double Sin(double x){
		return Math.sin(x);
	}

	public static double Cos(double x){
		return Math.cos(x);
	}

	public static double Tan(double x){
		return Math.tan(x);
	}

	public static double Asin(double x){
		return Math.asin(x);
	}

	public static double Acos(double x){
		return Math.acos(x);
	}

	public static double Atan(double x){
		return Math.atan(x);
	}

	public static double Atan2(double y, double x){
		double a;

		/* Atan2 is an invalid operation when x = 0 and y = 0, but this method does not return errors.*/
		a = 0d;

		if(x > 0d){
			a = Atan(y/x);
		}else if(x < 0d && y >= 0d){
			a = Atan(y/x) + Math.PI;
		}else if(x < 0d && y < 0d){
			a = Atan(y/x) - Math.PI;
		}else if(x == 0d && y > 0d){
			a = Math.PI/2d;
		}else if(x == 0d && y < 0d){
			a = -Math.PI/2d;
		}

		return a;
	}

	public static double Squareroot(double x){
		return Math.sqrt(x);
	}

	public static double Exp(double x){
		return Math.exp(x);
	}

	public static boolean DivisibleBy(double a, double b){
		return ((a%b) == 0d);
	}

	public static double Combinations(double n, double k){
		double i, j, c;

		c = 1d;
		j = 1d;
		i = n - k + 1d;

		for(; i <= n; ){
			c = c*i;
			c = c/j;

			i = i + 1d;
			j = j + 1d;
		}

		return c;
	}

	public static double Permutations(double n, double k){
		double i, c;

		c = 1d;

		for(i = n - k + 1d; i <= n; i = i + 1d){
			c = c*i;
		}

		return c;
	}

	public static boolean EpsilonCompare(double a, double b, double epsilon){
		return Math.abs(a - b) < epsilon;
	}

	public static double GreatestCommonDivisor(double a, double b){
		double t;

		for(; b != 0d; ){
			t = b;
			b = a%b;
			a = t;
		}

		return a;
	}

	public static double GCDWithSubtraction(double a, double b){
		double g;

		if(a == 0d){
			g = b;
		}else{
			for(; b != 0d; ){
				if(a > b){
					a = a - b;
				}else{
					b = b - a;
				}
			}

			g = a;
		}

		return g;
	}

	public static boolean IsInteger(double a){
		return (a - Math.floor(a)) == 0d;
	}

	public static boolean GreatestCommonDivisorWithCheck(double a, double b, NumberReference gcdReference){
		boolean success;
		double gcd;

		if(IsInteger(a) && IsInteger(b)){
			gcd = GreatestCommonDivisor(a, b);
			gcdReference.numberValue = gcd;
			success = true;
		}else{
			success = false;
		}

		return success;
	}

	public static double LeastCommonMultiple(double a, double b){
		double lcm;

		if(a > 0d && b > 0d){
			lcm = Math.abs(a*b)/GreatestCommonDivisor(a, b);
		}else{
			lcm = 0d;
		}

		return lcm;
	}

	public static double Sign(double a){
		double s;

		if(a > 0d){
			s = 1d;
		}else if(a < 0d){
			s = -1d;
		}else{
			s = 0d;
		}

		return s;
	}

	public static double Max(double a, double b){
		return Math.max(a, b);
	}

	public static double Min(double a, double b){
		return Math.min(a, b);
	}

	public static double Power(double a, double b){
		return Math.pow(a, b);
	}

	public static double Gamma(double x){
		return LanczosApproximation(x);
	}

	public static double LogGamma(double x){
		return Math.log(Gamma(x));
	}

	public static double LanczosApproximation(double z){
		double [] p;
		double i, y, t, x;

		p = new double [8];
		p[0] = 676.5203681218851;
		p[1] = -1259.1392167224028;
		p[2] = 771.32342877765313;
		p[3] = -176.61502916214059;
		p[4] = 12.507343278686905;
		p[5] = -0.13857109526572012;
		p[6] = 9.9843695780195716e-6;
		p[7] = 1.5056327351493116e-7;

		if(z < 0.5){
			y = Math.PI/(Math.sin(Math.PI*z)*LanczosApproximation(1d - z));
		}else{
			z = z - 1d;
			x = 0.99999999999980993;
			for(i = 0d; i < p.length; i = i + 1d){
				x = x + p[(int)(i)]/(z + i + 1d);
			}
			t = z + p.length - 0.5;
			y = Math.sqrt(2d*Math.PI)*Math.pow(t, z + 0.5)*Math.exp(-t)*x;
		}

		return y;
	}

	public static double Beta(double x, double y){
		return Gamma(x)*Gamma(y)/Gamma(x + y);
	}

	public static double Sinh(double x){
		return (Math.exp(x) - Math.exp(-x))/2d;
	}

	public static double Cosh(double x){
		return (Math.exp(x) + Math.exp(-x))/2d;
	}

	public static double Tanh(double x){
		return Sinh(x)/Cosh(x);
	}

	public static double Cot(double x){
		return 1d/Math.tan(x);
	}

	public static double Sec(double x){
		return 1d/Math.cos(x);
	}

	public static double Csc(double x){
		return 1d/Math.sin(x);
	}

	public static double Coth(double x){
		return Cosh(x)/Sinh(x);
	}

	public static double Sech(double x){
		return 1d/Cosh(x);
	}

	public static double Csch(double x){
		return 1d/Sinh(x);
	}

	public static double Error(double x){
		double y, t, tau, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10;

		if(x == 0d){
			y = 0d;
		}else if(x < 0d){
			y = -Error(-x);
		}else{
			c1 = -1.26551223;
			c2 = +1.00002368;
			c3 = +0.37409196;
			c4 = +0.09678418;
			c5 = -0.18628806;
			c6 = +0.27886807;
			c7 = -1.13520398;
			c8 = +1.48851587;
			c9 = -0.82215223;
			c10 = +0.17087277;

			t = 1d/(1d + 0.5*Math.abs(x));

			tau = t*Math.exp(-Math.pow(x, 2d) + c1 + t*(c2 + t*(c3 + t*(c4 + t*(c5 + t*(c6 + t*(c7 + t*(c8 + t*(c9 + t*c10)))))))));

			y = 1d - tau;
		}

		return y;
	}

	public static double ErrorInverse(double x){
		double y, a, t;

		a = (8d*(Math.PI - 3d))/(3d*Math.PI*(4d - Math.PI));

		t = 2d/(Math.PI*a) + Math.log(1d - Math.pow(x, 2d))/2d;
		y = Sign(x)*Math.sqrt(Math.sqrt(Math.pow(t, 2d) - Math.log(1d - Math.pow(x, 2d))/a) - t);

		return y;
	}

	public static double FallingFactorial(double x, double n){
		double k, y;

		y = 1d;

		for(k = 0d; k <= n - 1d; k = k + 1d){
			y = y*(x - k);
		}

		return y;
	}

	public static double RisingFactorial(double x, double n){
		double k, y;

		y = 1d;

		for(k = 0d; k <= n - 1d; k = k + 1d){
			y = y*(x + k);
		}
                //System.out.println("400 Distributions_2F1 Rising factorial, y = " + y);
		return y;
	}

	public static double Hypergeometric(double a, double b, double c, double z, double maxIterations, double precision){
		double y;

		if(Math.abs(z) >= 0.5){
			y = Math.pow(1d - z, -a)*HypergeometricDirect(a, c - b, c, z/(z - 1d), maxIterations, precision);
		}else{
			y = HypergeometricDirect(a, b, c, z, maxIterations, precision);
		}
                //System.out.println("412 Distributions_2F1, Hypergeometric: y = " + y);
		return y;
	}

	public static double HypergeometricDirect(double a, double b, double c, double z, double maxIterations, double precision){
		double y, yp, n;
		boolean done;

		y = 0d;
		done = false;

		for(n = 0d; n < maxIterations && !done; n = n + 1d){
                    //System.out.println("424 Distributions_2F1, HypergeometricDirect y in loop = " + y);
                    yp = RisingFactorial(a, n)*RisingFactorial(b, n)/RisingFactorial(c, n)*Math.pow(z, n)/Factorial(n);
                    //System.out.println("426 Distributions_2F1, HypergeometricDirect yp in loop = " + yp);
                    if(Math.abs(yp) < precision){
                            done = true;
                    }
                    y = y + yp;
                    //System.out.println("431 Distributions_2F1, HypergeometricDirect y in loop = " + y);
		}
                //System.out.println("433 Distributions_2F1, HypergeometricDirect y = " + y);
		return y;
	}

	public static double test(){
		double failures, value;
		boolean b;
		NumberReference numberReference;

		failures = 0d;
		numberReference = CreateNumberReference(0d);

		failures = failures + testLog();
		failures = failures + testCombinations();
		failures = failures + testGamma();
		failures = failures + testError();
		failures = failures + testErrorInverse();
		failures = failures + testFactorials();
		failures = failures + testHypergeometric();
		failures = failures + testBeta();

		value = Absolute(-5d);
		value = Acos(0.5);
		value = Atan(0.5);
		value = Atan2(0.5, 0.5);
		value = Ceil(1.1);
		value = Combinations(3d, 5d);
		value = Cos(0.5);
		b = DivisibleBy(5d, 2d);
		b = EpsilonCompare(1.1, 1.2, 0.15);
		value = Exp(2d);
		value = Floor(1.9);
		b = GreatestCommonDivisorWithCheck(100d, 20d, numberReference);
		b = IsInteger(1.1);
		value = LeastCommonMultiple(20d, 10d);
		value = Logarithm(100d);
		value = NaturalLogarithm(2.89);
		value = Negate(5d);
		value = Positive(-8d);
		value = Round(1.5);
		value = BankersRound(1.5);
		value = Sign(-6d);
		value = Sin(4d);
		value = Squareroot(4d);
		value = Tan(0.5);
		value = Truncate(1.56);

		value = GreatestCommonDivisor(42d, 56d);

		if(value != 14d){
			failures = failures + 1d;
		}

		value = GreatestCommonDivisor(6d, 15d);

		if(value != 3d){
			failures = failures + 1d;
		}

		value = GCDWithSubtraction(42d, 56d);

		if(value != 14d){
			failures = failures + 1d;
		}

		value = GCDWithSubtraction(6d, 15d);

		if(value != 3d){
			failures = failures + 1d;
		}

		return failures;
	}

	public static double testLog(){
		double answer, failures;

		failures = 0d;

		answer = Logarithm(100d);

		if(answer != 2d){
			failures = failures + 1d;
		}

		return failures;
	}

	public static double testCombinations(){
		double answer, failures;

		failures = 0d;

		answer = Combinations(52d, 5d);

		if(answer != 2598960d){
			failures = failures + 1d;
		}

		return failures;
	}

	public static double testGamma(){
		double g, failures;

		failures = 0d;

		g = Gamma(1d);

		if(!EpsilonCompare(g, 1d, 0.01)){
			failures = failures + 1d;
		}

		g = Gamma(5.5);

		if(!EpsilonCompare(g, 52.3428, 0.01)){
			failures = failures + 1d;
		}

		return failures;
	}

	public static double testError(){
		double g, failures;

		failures = 0d;

		g = Error(1d);

		if(!EpsilonCompare(g, 0.84270079294971, 0.0001)){
			failures = failures + 1d;
		}

		g = Error(-0.5);

		if(!EpsilonCompare(g, -0.520500, 0.01)){
			failures = failures + 1d;
		}

		return failures;
	}

	public static double testErrorInverse(){
		double g, failures;

		failures = 0d;

		g = ErrorInverse(0.84270079294971);

		if(!EpsilonCompare(g, 1d, 0.001)){
			failures = failures + 1d;
		}

		g = ErrorInverse(-0.520500);

		if(!EpsilonCompare(g, -0.5, 0.001)){
			failures = failures + 1d;
		}

		return failures;
	}

	public static double testFactorials(){
		double y, failures;

		failures = 0d;

		y = Factorial(5d);

		if(!EpsilonCompare(y, 120d, 0.001)){
			failures = failures + 1d;
		}

		y = RisingFactorial(5d, 2d);

		if(!EpsilonCompare(y, 30d, 0.001)){
			failures = failures + 1d;
		}

		y = FallingFactorial(5d, 2d);

		if(!EpsilonCompare(y, 20d, 0.001)){
			failures = failures + 1d;
		}

		return failures;
	}

	public static double testHypergeometric(){
		double x, a, y, yf, failures;

		failures = 0d;

		/* asin(x)*/
		x = 0.5;
		y = x*Hypergeometric(0.5, 0.5, 1.5, Math.pow(x, 2d), 50d, 0.0001);
		yf = Asin(x);

		if(!EpsilonCompare(y, yf, 0.001)){
			failures = failures + 1d;
		}

		/* (1 - x)^-a*/
		x = 0.5;
		a = 0.5;
		y = Hypergeometric(a, 1d, 1d, x, 50d, 0.0001);
		yf = Math.pow(1d - x, -a);

		if(!EpsilonCompare(y, yf, 0.001)){
			failures = failures + 1d;
		}

		/* ln(1 + x)*/
		x = 0.5;
		y = x*Hypergeometric(1d, 1d, 2d, -x, 50d, 0.0001);
		yf = Math.log(1d + x);

		if(!EpsilonCompare(y, yf, 0.001)){
			failures = failures + 1d;
		}

		/* Other*/
		y = Hypergeometric(0.5, 1.5, 1.5, -1d, 50d, 0.0001);
		yf = 0.707107;

		if(!EpsilonCompare(y, yf, 0.001)){
			failures = failures + 1d;
		}

		y = Hypergeometric(0.5, 1.5, 1.5, -12.5, 50d, 0.0001);
		yf = 0.272166;

		if(!EpsilonCompare(y, yf, 0.001)){
			failures = failures + 1d;
		}

		return failures;
	}

	public static double testBeta(){
		double g, failures;

		failures = 0d;

		g = Beta(3d, 4d);

		if(!EpsilonCompare(g, 1d/60d, 0.001)){
			failures = failures + 1d;
		}

		g = Beta(0.5, 1.5);

		if(!EpsilonCompare(g, 1.57080, 0.001)){
			failures = failures + 1d;
		}

		return failures;
	}

static public class BooleanArrayReference{
	public boolean [] booleanArray;
}
static public class BooleanReference{
	public boolean booleanValue;
}
static public class CharacterReference{
	public char characterValue;
}
static public class NumberArrayReference{
	public double [] numberArray;
}
static public class NumberReference{
	public double numberValue;
}
static public class StringArrayReference{
	public StringReference [] stringArray;
}
static public class StringReference{
	public char [] string;
}
	public static BooleanReference CreateBooleanReference(boolean value){
		BooleanReference ref;
		ref = new BooleanReference();
		ref.booleanValue = value;

		return ref;
	}

	public static BooleanArrayReference CreateBooleanArrayReference(boolean [] value){
		BooleanArrayReference ref;
		ref = new BooleanArrayReference();
		ref.booleanArray = value;

		return ref;
	}

	public static BooleanArrayReference CreateBooleanArrayReferenceLengthValue(double length, boolean value){
		BooleanArrayReference ref;
		double i;
		ref = new BooleanArrayReference();
		ref.booleanArray = new boolean [(int)(length)];

		for(i = 0d; i < length; i = i + 1d){
			ref.booleanArray[(int)(i)] = value;
		}

		return ref;
	}

	public static void FreeBooleanArrayReference(BooleanArrayReference booleanArrayReference){
		delete(booleanArrayReference.booleanArray);
		delete(booleanArrayReference);
	}

	public static CharacterReference CreateCharacterReference(char value){
		CharacterReference ref;
		ref = new CharacterReference();
		ref.characterValue = value;

		return ref;
	}

	public static NumberReference CreateNumberReference(double value){
		NumberReference ref;
		ref = new NumberReference();
		ref.numberValue = value;

		return ref;
	}

	public static NumberArrayReference CreateNumberArrayReference(double [] value){
		NumberArrayReference ref;
		ref = new NumberArrayReference();
		ref.numberArray = value;

		return ref;
	}

	public static NumberArrayReference CreateNumberArrayReferenceLengthValue(double length, double value){
		NumberArrayReference ref;
		double i;
		ref = new NumberArrayReference();
		ref.numberArray = new double [(int)(length)];

		for(i = 0d; i < length; i = i + 1d){
			ref.numberArray[(int)(i)] = value;
		}

		return ref;
	}

	public static void FreeNumberArrayReference(NumberArrayReference numberArrayReference){
		delete(numberArrayReference.numberArray);
		delete(numberArrayReference);
	}

	public static StringReference CreateStringReference(char [] value){
		StringReference ref;
		ref = new StringReference();
		ref.string = value;

		return ref;
	}

	public static StringReference CreateStringReferenceLengthValue(double length, char value){
		StringReference ref;
		double i;
		ref = new StringReference();
		ref.string = new char [(int)(length)];

		for(i = 0d; i < length; i = i + 1d){
			ref.string[(int)(i)] = value;
		}

		return ref;
	}

	public static void FreeStringReference(StringReference stringReference){
		delete(stringReference.string);
		delete(stringReference);
	}

	public static StringArrayReference CreateStringArrayReference(StringReference [] strings){
		StringArrayReference ref;
		ref = new StringArrayReference();
		ref.stringArray = strings;

		return ref;
	}

	public static StringArrayReference CreateStringArrayReferenceLengthValue(double length, char [] value){
		StringArrayReference ref;
		double i;
		ref = new StringArrayReference();
		ref.stringArray = new StringReference [(int)(length)];

		for(i = 0d; i < length; i = i + 1d){
			ref.stringArray[(int)(i)] = CreateStringReference(value);
		}

		return ref;
	}

	public static void FreeStringArrayReference(StringArrayReference stringArrayReference){
		double i;
		for(i = 0d; i < stringArrayReference.stringArray.length; i = i + 1d){
			delete(stringArrayReference.stringArray[(int)(i)]);
		}
		delete(stringArrayReference.stringArray);
		delete(stringArrayReference);
	}

  public static void delete(Object object){
    // Java has garbage collection.   
    }
}
