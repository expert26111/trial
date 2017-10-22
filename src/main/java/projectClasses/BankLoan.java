
package projectClasses;

import java.io.Serializable;
import java.util.logging.Logger;

public class BankLoan  implements Serializable
{
    private String ssn;
    private int creaditScore;
    private double loanAmount;
    private double loanDuration;

    public BankLoan(String ssn, int creaditScore, double loanAmount, double loanDuration) 
    {
        this.ssn = ssn;
        this.creaditScore = creaditScore;
        this.loanAmount = loanAmount;
        this.loanDuration = loanDuration;
    }

    public BankLoan() 
    {
        
    }
  
    
    
    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public int getCreaditScore() {
        return creaditScore;
    }

    public void setCreaditScore(int creaditScore) {
        this.creaditScore = creaditScore;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getLoanDuration() {
        return loanDuration;
    }

    public void setLoanDuration(double loanDuration) {
        this.loanDuration = loanDuration;
    }

    @Override
    public String toString() 
    {
        return "BankLoan{" + "ssn=" + ssn + ", creaditScore=" + creaditScore + ", loanAmount=" + loanAmount + ", loanDuration=" + loanDuration + '}';
    }
    
    
    
   
    
}
