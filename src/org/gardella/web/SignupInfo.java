package org.gardella.web;

public class SignupInfo {

    
    private String email;
    private String clearPassword;
    private String cell;
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getClearPassword() {
        return clearPassword;
    }
    public void setClearPassword(String clearPassword) {
        this.clearPassword = clearPassword;
    }
    public String getCell() {
        return cell;
    }
    public void setCell(String cell) {
        cell = cell.replaceAll( "[^\\d]", "" ); //strip out all non-numeric chars
        this.cell = cell;
    }
    
}
