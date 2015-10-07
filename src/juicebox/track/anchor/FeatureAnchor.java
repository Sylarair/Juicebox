/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2015 Broad Institute, Aiden Lab
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package juicebox.track.anchor;

/**
 * Created by muhammadsaadshamim on 9/28/15.
 */
public class FeatureAnchor implements Comparable<FeatureAnchor> {

    private int x1, x2;
    private String chr;

    /**
     * Inititalize anchor given parameters (e.g. from BED file)
     *
     * @param chr
     * @param x1
     * @param x2
     */
    public FeatureAnchor(String chr, int x1, int x2) {
        this.chr = chr;
        if (x1 < x2) {
            // x1 < x2
            this.x1 = x1;
            this.x2 = x2;
        } else {
            // x2 < x1 shouldn't ever happen, but just in case
            System.err.println("Improperly formatted BED file or anchor list");
            //this.x1 = x2;
            //this.x2 = x1;
        }
    }

    /**
     * @return copy of this anchor
     */
    public FeatureAnchor clone() {
        return new FeatureAnchor(chr, x1, x2);
    }

    /**
     * @return chromosome name
     */
    public String getChr() {
        return chr;
    }

    /**
     * @return start point
     */
    public int getX1() {
        return x1;
    }

    /**
     * @return end point
     */
    public int getX2() {
        return x2;
    }

    /**
     * @return width of this anchor
     */
    public int getWidth() {
        return x2 - x1;
    }

    /**
     * Expand this anchor (symmetrically) by the width given
     *
     * @param width
     */
    public void widenMargins(int width) {
        x1 = x1 - width / 2;
        x2 = x2 + width / 2;
    }

    /**
     * @param x
     * @return true if x is within bounds of anchor
     */
    private boolean contains(int x) {
        return x >= x1 && x <= x2;
    }

    /**
     * @param anchor
     * @return true if this is strictly left of given anchor
     */
    public boolean isStrictlyToTheLeftOf(FeatureAnchor anchor) {
        return x2 < anchor.x1;
    }

    /**
     * @param anchor
     * @return true if this is strictly right of given anchor
     */
    public boolean isStrictlyToTheRightOf(FeatureAnchor anchor) {
        return anchor.x2 < x1;
    }

    /**
     * @param anchor
     * @return true if given anchor overlaps at either edge with this anchor
     */
    public boolean hasOverlapWith(FeatureAnchor anchor) {
        if (chr.equals(anchor.chr)) {
            return this.contains(anchor.x1) || this.contains(anchor.x2);
        }
        return false;
    }

    public void mergeWith(FeatureAnchor anchor) {
        if (chr.equals(anchor.chr)) {
            x1 = Math.min(x1, anchor.x1);
            x2 = Math.max(x2, anchor.x2);
        } else {
            System.err.println("Attempted to merge anchors on different chromosomes");
            System.err.println(this + " & " + anchor);
        }
    }

    @Override
    public String toString() {
        return chr + "\t" + x1 + "\t" + x2;
    }

    @Override
    public boolean equals(Object obj) {
        FeatureAnchor o = (FeatureAnchor) obj;
        return (chr.equals(o.chr) && x1 == o.x1 && x2 == o.x2);
    }

    @Override
    public int hashCode() {
        return x2 * chr.hashCode() + x1;
    }

    @Override
    public int compareTo(FeatureAnchor o) {
        if (chr.equals(o.chr)) {
            if (x1 == o.x1) {
                return (new Integer(x2)).compareTo(o.x2);
            }
            return (new Integer(x1)).compareTo(o.x1);
        }
        return chr.compareTo(o.chr);
    }
}