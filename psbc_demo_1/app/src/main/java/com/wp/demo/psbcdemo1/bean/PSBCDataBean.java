package com.wp.demo.psbcdemo1.bean;

import java.util.List;

/**
 * Created by wangpeng on 15-4-3.
 */
public class PSBCDataBean {
    Personals personals;
    List<CompanyDataBean> companyDataBeans;

    public void setPersonalBean(Personals personals){
        this.personals = personals;
    }

    public Personals getPersonalsBean() {
        return personals;
    }

    public void setCompanyDataBeans(List<CompanyDataBean> companyDataBeans) {
        this.companyDataBeans = companyDataBeans;
    }

    public List<CompanyDataBean> getCompanyDataBeans() {
        return this.companyDataBeans;
    }
}
