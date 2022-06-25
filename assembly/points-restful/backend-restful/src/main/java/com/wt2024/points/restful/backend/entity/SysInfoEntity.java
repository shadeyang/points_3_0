package com.wt2024.points.restful.backend.entity;

public class SysInfoEntity {
    private String custno;

    private String type;

    private String institution;

    private String custaddress;

    private String custrequestip;

    private String privatekey;

    private String publickey;

    private String threedeskey;

    private String switchconf;

    public SysInfoEntity(String custno, String type, String institution, String custaddress, String custrequestip, String privatekey, String publickey, String threedeskey, String switchconf) {
        this.custno = custno;
        this.type = type;
        this.institution = institution;
        this.custaddress = custaddress;
        this.custrequestip = custrequestip;
        this.privatekey = privatekey;
        this.publickey = publickey;
        this.threedeskey = threedeskey;
        this.switchconf = switchconf;
    }

    public SysInfoEntity() {
        super();
    }

    public String getCustno() {
        return custno;
    }

    public void setCustno(String custno) {
        this.custno = custno == null ? null : custno.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution == null ? null : institution.trim();
    }

    public String getCustaddress() {
        return custaddress;
    }

    public void setCustaddress(String custaddress) {
        this.custaddress = custaddress == null ? null : custaddress.trim();
    }

    public String getCustrequestip() {
        return custrequestip;
    }

    public void setCustrequestip(String custrequestip) {
        this.custrequestip = custrequestip == null ? null : custrequestip.trim();
    }

    public String getPrivatekey() {
        return privatekey;
    }

    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey == null ? null : privatekey.trim();
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey == null ? null : publickey.trim();
    }

    public String getThreedeskey() {
        return threedeskey;
    }

    public void setThreedeskey(String threedeskey) {
        this.threedeskey = threedeskey == null ? null : threedeskey.trim();
    }

    public String getSwitchconf() {
        return switchconf;
    }

    public void setSwitchconf(String switchconf) {
        this.switchconf = switchconf == null ? null : switchconf.trim();
    }
}