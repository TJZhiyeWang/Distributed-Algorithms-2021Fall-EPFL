package cs451.Utils;

public class Pair {
    public int fst; // proc
    public int snd; // seq
    public Pair(int fst, int snd){
        this.fst = fst;
        this.snd = snd;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof Pair){
            return this.fst == ((Pair) obj).fst && this.snd == ((Pair) obj).snd;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return fst*snd;
    }

}

