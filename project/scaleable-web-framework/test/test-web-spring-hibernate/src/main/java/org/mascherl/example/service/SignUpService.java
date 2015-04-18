/*
 * Copyright 2015, Jakob Korherr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
