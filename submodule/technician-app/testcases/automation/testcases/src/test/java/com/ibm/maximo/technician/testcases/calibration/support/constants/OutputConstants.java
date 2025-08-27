package com.ibm.maximo.technician.testcases.calibration.support.constants;

import java.util.HashMap;
import java.util.Map;

public final class OutputConstants {
	
	// Define a map to hold XPaths for AsFound and AsLeft mappings
    private static final Map<String, String> asFoundLocators = new HashMap<>();
    private static final Map<String, String> asLeftLocators = new HashMap<>();
    private static final Map<String, String> DisasFoundLocators = new HashMap<>();
    private static final Map<String, String> DisasLeftLocators = new HashMap<>();

    static {
        // As Found mapping
    	asFoundLocators.put("CalPointList", "(.//div[contains(@id, 'qqkbe')]//li)[%s]");
        asFoundLocators.put("SectionLabel", ".//p[@id='v_3k8[%s]']");
        asFoundLocators.put("SectionBtn", ".//button[@id='mxa3_[%s]']//*[@id='mxa3_[%s]_icon']");
        asFoundLocators.put("NominalInput", "(//li)[%s]//input[starts-with(@id, 'z2py3[')]");
        asFoundLocators.put("DesiredOutput", "(//li)[%s]//input[starts-with(@id, 'qgy3d[')]");
        asFoundLocators.put("ToleranceSectionIcon", ".//*[@id='q298e[%s]_icon']");
        asFoundLocators.put("ToleranceSectionBtn", ".//button[@id='q298e[%s]']");
        asFoundLocators.put("Tol1FromValue", ".//p[@id='qmkv8[%s]_fieldValue0']");
        asFoundLocators.put("Tol1ToValue", ".//p[@id='yv2ez[%s]_fieldValue0']");
        asFoundLocators.put("Tol1ErrorValue", ".//p[@id='e84eq[%s]_fieldValue0']");
        asFoundLocators.put("Tol2FromValue", ".//p[@id='zw3yz[%s]_fieldValue0']");
        asFoundLocators.put("Tol2ToValue", ".//p[@id='rrbpg[%s]_fieldValue0']");
        asFoundLocators.put("Tol2ErrorValue", ".//p[@id='exb4z[%s]_fieldValue0']");
        asFoundLocators.put("Tol3FromValue", ".//p[@id='jgqz6[%s]_fieldValue0']");
        asFoundLocators.put("Tol3ToValue", ".//p[@id='pjv9d[%s]_fieldValue0']");
        asFoundLocators.put("Tol3ErrorValue", ".//p[@id='px7rq[%s]_fieldValue0']");
        asFoundLocators.put("Tol4FromValue", ".//p[@id='n636j[%s]_fieldValue0']");
        asFoundLocators.put("Tol4ToValue", ".//p[@id='yvrpp[%s]_fieldValue0']");
        asFoundLocators.put("Tol4ErrorValue", ".//p[@id='e9qdb[%s]_fieldValue0']");
        asFoundLocators.put("AssetErrorValue", ".//p[@id='q2nx9[%s]_fieldValue0']");
        asFoundLocators.put("ProcessErrorValue", ".//p[@id='g6rd3[%s]_fieldValue0']");
        asFoundLocators.put("ToleranceIcon", ".//*[contains(@id, 'vav23')]");
        asFoundLocators.put("ToleranceIconBottom", ".//*[contains(@id, 'x5x5j')]");
        asFoundLocators.put("ToleranceExceedMessage", ".//*[contains(@id, 'k_k7z')]");
        asFoundLocators.put("DiscardSaveButton", ".//*[@id='saveDiscardRules_button_group_saveDiscardRules_secondary_button']");
        asFoundLocators.put("NoAdjInlineNotification", "pv6ra");
        asFoundLocators.put("AddCalibrationPointTile", "bdq2n");
        asFoundLocators.put("addCalPointBtnIdLocator","pknwa");
        
        
        // As Left mapping
        asLeftLocators.put("CalPointList", "(.//div[contains(@id, 'z5bj_')]//li)[%s]");
        asLeftLocators.put("SectionLabel", ".//p[@id='aa75b[%s]']");
        asLeftLocators.put("SectionBtn", ".//button[@id='wwd_6[%s]']//*[@id='wwd_6[%s]_icon']");
        asLeftLocators.put("NominalInput", "(//li)[%s]//input[starts-with(@id, 'qe3y5[')]");
        asLeftLocators.put("DesiredOutput", "(//li)[%s]//input[starts-with(@id, 'mw4rq[')]");
        asLeftLocators.put("ToleranceSectionIcon", ".//*[@id='vqwdy[%s]_icon']");
        asLeftLocators.put("ToleranceSectionBtn", ".//button[@id='vqwdy[%s]']");
        asLeftLocators.put("Tol1FromValue", ".//p[@id='eww8x[%s]_fieldValue0']");
        asLeftLocators.put("Tol1ToValue", ".//p[@id='adx2w[%s]_fieldValue0']");
        asLeftLocators.put("Tol1ErrorValue", ".//p[@id='ad3x5[%s]_fieldValue0']");
        asLeftLocators.put("Tol2FromValue", ".//p[@id='aqk2p[%s]_fieldValue0']");
        asLeftLocators.put("Tol2ToValue", ".//p[@id='w5ezb[%s]_fieldValue0']");
        asLeftLocators.put("Tol2ErrorValue", ".//p[@id='kkqb9[%s]_fieldValue0']");
        asLeftLocators.put("Tol3FromValue", ".//p[@id='v6e99[%s]_fieldValue0']");
        asLeftLocators.put("Tol3ToValue", ".//p[@id='debz_[%s]_fieldValue0']");
        asLeftLocators.put("Tol3ErrorValue", ".//p[@id='aaky2[%s]_fieldValue0']");
        asLeftLocators.put("Tol4FromValue", ".//p[@id='w6abv[%s]_fieldValue0']");
        asLeftLocators.put("Tol4ToValue", ".//p[@id='xgby2[%s]_fieldValue0']");
        asLeftLocators.put("Tol4ErrorValue", ".//p[@id='xnw3k[%s]_fieldValue0']");
        asLeftLocators.put("AssetErrorValue", ".//p[@id='n_97j[%s]_fieldValue0']");
        asLeftLocators.put("ProcessErrorValue", ".//p[@id='n7pz5[%s]_fieldValue0']");
        asLeftLocators.put("ToleranceIcon", ".//*[contains(@id, 'pv6ey')]");
        asLeftLocators.put("ToleranceIconBottom", ".//*[contains(@id, 'ya9m8')]");
        asLeftLocators.put("ToleranceExceedMessage", ".//*[contains(@id, 'xqyz9')]");
        asLeftLocators.put("DiscardSaveButton", ".//*[@id='saveAsLeftDiscardRules_button_group_saveAsLeftDiscardRules_secondary_button']");
        asLeftLocators.put("NoAdjInlineNotification", "p9rey");
        asLeftLocators.put("AddCalibrationPointTile", "zmpnr");
        asLeftLocators.put("addCalPointBtnIdLocator","zv7_a");
        
        // Discrete As Found mapping
        DisasFoundLocators.put("NominalInput", "(.//input[starts-with(@id, 'vn_2w[')])[%s]");
        
        // Discrete As Left mapping
        DisasLeftLocators.put("NominalInput", "(.//input[starts-with(@id, 'x2qen[')])[%s]");
    }

    // Private constructor to prevent instantiation
    private OutputConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Retrieves the XPath for a given type and locator key.
     * 
     * @param type The type of the page ("AsFound" or "AsLeft").
     * @param locatorKey The key for the specific XPath locator.
     * @param params Parameters to replace in the XPath.
     * @return The XPath string with placeholders replaced by provided parameters.
     */
    public static String getXPath(String type, String locatorKey, Object... params) {
    	Map<String, String> locators;
    	boolean discreteLocator = type.contains("Dis");
    	
    	if (discreteLocator) {
    		locators = "DisAsFound".equals(type) ? DisasFoundLocators : DisasLeftLocators;
    	} else {
    		locators = "AsFound".equals(type) ? asFoundLocators : asLeftLocators;
    	}
        
        String locator = locators.get(locatorKey);
        
        // if locator does not exist for discrete, try general locator
        if (locator == null && discreteLocator) {
        	if(type.contains("AsFound"))
        		locator = asFoundLocators.get(locatorKey);
        	else
        		locator = asLeftLocators.get(locatorKey);	
        }
        
        if(locator == null)
    		throw new IllegalArgumentException("Invalid locator key for type " + type + ": " + locatorKey);

        for (int i = 0; i < params.length; i++) {
            locator = locator.replace("%" + (i + 1), String.valueOf(params[i]));
        }

        return locator;
    }
}