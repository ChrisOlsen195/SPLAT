/**************************************************
 *           HorizontalPositioner                 *
 *                  11/01/23                      *
 *                    00:00                       *
 *************************************************/
package utilityClasses;

import javafx.geometry.Point2D;

    public class HorizontalPositioner {
        
        int nBars, nLevelsA, nLevelsB;
        double rangeOfBars;
        double endRangeBarFrac = 0.2; // in fraction of bar width
        double betweenBarsBarFrac = 0.1; // in fraction of bar width
        double fullBarWidth, halfBarWidth, fracBarWidth, confIntEndWidthFrac ;
          
        public HorizontalPositioner(int nLevelsOfFactorA, int nLevelsOfFactorB, double rangeBtwnBigTicks) { 
            nLevelsA = nLevelsOfFactorA;            
            nLevelsB = nLevelsOfFactorB;            
            nBars = nLevelsA * nLevelsB;
            rangeOfBars = rangeBtwnBigTicks;           
            double denom = 2  * endRangeBarFrac + nLevelsB + (nLevelsB - 1.0) * betweenBarsBarFrac; // Check this!!
            fullBarWidth = rangeBtwnBigTicks / denom;   //  Bar width if all space used for bar
            halfBarWidth = 0.5 * fullBarWidth;
            fracBarWidth = 0.75 * fullBarWidth; // Controls the horizontal room to draw
            confIntEndWidthFrac = 0.20 * fullBarWidth; // Controls the top & bottom horizontals 
        }
        
        public double GetEndSpace() {return endRangeBarFrac; }
        public void SetEndSpace(double fractionOfBar) {
            endRangeBarFrac = fractionOfBar;
        }
        
        public double getBarWidth() { return fullBarWidth; }        
        public double getCIEndWidthFrac() { return confIntEndWidthFrac; }        
        public double getMidBarPosition(int ith, double daMid) {
            double midBarPosition = 0.0;
            double m = 0.; //    slope
            double b = 0.; // intercept
            int half_nLevelsB = nLevelsB / 2;   //  round down
            int relativePositionInA = ith % nLevelsB;
            Point2D leftX_and_Width;            
            if (relativePositionInA == 0 )
                relativePositionInA = nLevelsB;             
            if ((nLevelsB / 2) * 2 == nLevelsB)  {// Even # of B levels
                m = fullBarWidth;
                b = daMid - 0.5 * fullBarWidth - half_nLevelsB * fullBarWidth;
                midBarPosition = m * relativePositionInA + b;
            }
            else {  // Odd # of B levels
                m = fullBarWidth;
                b = daMid - fullBarWidth * (half_nLevelsB + 1.0);
                midBarPosition = m * relativePositionInA + b;
            }   
            return midBarPosition;
        }
        
        public double GetBetweenSpace() {return betweenBarsBarFrac; }        
        public void SetBetweenSpace(double fractionOfBar) {
            betweenBarsBarFrac = fractionOfBar;
        }
        
        public double GetRangeBetweenCatTicks() {return betweenBarsBarFrac; }
        
        public void SetRangeBetweenCatTicks(double fractionOfBar) {
            betweenBarsBarFrac = fractionOfBar;
        }    
        
        // Used with categorical horizontal axes, e.g. ANOVA
        public Point2D getIthLeftPosition(int ith, double daMid) {
            double ithLeft = 0.0;
            double midBarPosition = 0.0;
            double m = 0.; //    slope
            double b = 0.; // intercept
            int half_nLevelsB = nLevelsB / 2;   //  round down
            int relativePositionInA = ith % nLevelsB;
            Point2D leftX_and_Width;
            
            if (relativePositionInA == 0 ) { relativePositionInA = nLevelsB; } 
            
            if ((nLevelsB / 2) * 2 == nLevelsB)  {// Even # of B levels
                m = fullBarWidth;
                b = daMid - 0.5 * fullBarWidth - half_nLevelsB * fullBarWidth;
                midBarPosition = m * relativePositionInA + b;
                ithLeft = midBarPosition - 0.5 * fracBarWidth;
            }
            else {  // Odd # of B levels
                m = fullBarWidth;
                b = daMid - fullBarWidth * (half_nLevelsB + 1.0);
                midBarPosition = m * relativePositionInA + b;
                ithLeft = midBarPosition - 0.5 * fracBarWidth;
            }
            
            double widthOfBar = 2.0 * (midBarPosition - ithLeft);
            leftX_and_Width = new Point2D(ithLeft, widthOfBar);            
            return leftX_and_Width;
        }
    }
