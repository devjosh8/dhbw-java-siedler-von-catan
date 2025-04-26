package de.svenojo.catan.math;

public class AxialVector {
    
    private int q,r;

    public AxialVector(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public AxialVector() {
        this.q = 0;
        this.r = 0;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public void setR(int r) {
        this.r = r;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof AxialVector)) return false;
        
        AxialVector other = (AxialVector) obj;
        return other.q == q && other.r == r;
    }
}
