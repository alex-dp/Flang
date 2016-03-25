package eu.depa.flang;

public class ResultLine {

    public String from, to;
    public Boolean correct;

    public ResultLine(String pFrom, String pTo, Boolean pCorrect) {
        super();
        this.from = pFrom;
        this.to = pTo;
        this.correct = pCorrect;
    }
}