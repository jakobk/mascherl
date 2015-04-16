package org.mascherl.example.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for sign up of new users.
 *
 * @author Jakob Korherr
 */
@Service
public class SignUpService {

    private static final Map<String, List<String>> countryStateIndex = new HashMap<>();
    static {
        countryStateIndex.put("Austria", Arrays.asList(
                "Vienna",
                "Lower Austria",
                "Upper Austria",
                "Salzburg",
                "Styria",
                "Tyrol",
                "Vorarlberg",
                "Burgenland",
                "Carinthia"
        ));
        countryStateIndex.put("Germany", Arrays.asList(
                "Baden-Württemberg",
                "Bavaria",
                "Berlin",
                "Brandenburg",
                "Bremen",
                "Hamburg",
                "Hesse",
                "Lower Saxony",
                "Mecklenburg-Vorpommern",
                "North Rhine-Westphalia",
                "Rhineland-Palatinate",
                "Saarland",
                "Saxony",
                "Saxony-Anhalt",
                "Schleswig-Holstein",
                "Thuringia"
        ));
    }

    public List<String> getCountries() {
        return Arrays.asList("Austria", "Germany", "other");
    }

    public List<String> getStates(String country) {
        List<String> states = countryStateIndex.get(country);
        if (states == null) {
            states = Arrays.asList("-");
        }
        return states;
    }


}
