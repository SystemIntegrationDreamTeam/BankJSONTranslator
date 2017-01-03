/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bankjsontranslator;

/**
 *
 * @author Buhrkall
 */
public class Result {
    
     
 public int ssn;
 public int creditScore;
 public double loanAmount;
 public int loanDuration;

    public Result(int ssn, int creditScore, double loanAmount, int loanDuration) {
        this.ssn = ssn;
        this.creditScore = creditScore;
        this.loanAmount = loanAmount;
        this.loanDuration = loanDuration;
    }

 @Override
    public String toString(){
     return "SSN : " +ssn + " CreditScore : " + creditScore + " Loan Amount :  " + loanAmount + " Loan Duration : " + loanDuration;
    }
    
}
