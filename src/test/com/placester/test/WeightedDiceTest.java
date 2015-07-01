package com.placester.test;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class WeightedDiceTest
{
    @Test
    public void test()
    {
        float errVal = .01f;
        float [] weights = new float[]{.05f,.10f,.15f,.2f, .2f, .3f};
        SixSidedWeightedDie theDie = new SixSidedWeightedDie(weights);
        int [] counts = new int []{0,0,0,0,0,0};
        int numThrows = 100000;
        for(int i = 0; i < numThrows; i++)
        {
            int val = theDie.throwDie();
            counts[val-1]++;
        }

        for(int i = 0; i < 6; i++)
        {
            float actual = (float)counts[i]/numThrows;
            float diff = Math.abs(actual - weights[i]);
            Assert.assertTrue(diff <= errVal);
        }
    }

    @Rule
    public ExpectedException except = ExpectedException.none();

    @Test
    public void testEpsilon() {
        float[] weights =
                new float[] {.05f, .10f, .15f, .2f, .2f, .30001f};
        // shouldn't throw exception even tho it doesn't sum to 1.0 exactly in binary
        new SixSidedWeightedDie(weights);
        weights =
                new float[] {.05f, .10f, .15f, .2f, .2f, .29999f};
        // shouldn't throw exception even tho it doesn't sum to 1.0 exactly in binary
        new SixSidedWeightedDie(weights);
    }

    @Test
    public void testNeed6() {
        except.expect(Exception.class);
        float[] weights = new float[] {.05f, .10f, .15f, .2f, .2f, .2f, .1f};
        new SixSidedWeightedDie(weights);
    }

    @Test
    public void testNoNegatives() {
        except.expect(Exception.class);
        float[] weights = new float[] {.05f, -.10f, .35f, .2f, .2f, .3f};
        new SixSidedWeightedDie(weights);
    }
}
