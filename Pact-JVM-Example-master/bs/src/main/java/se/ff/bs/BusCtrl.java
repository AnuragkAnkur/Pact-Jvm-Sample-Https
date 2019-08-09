package se.ff.bs;

import org.assertj.core.presentation.Representation;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
public class BusCtrl {



    @RequestMapping("/bus/{station}/{nr}")
    public BusInfo bus(@PathVariable String station, @PathVariable String nr) {
        int eta = getEtaBasedOnGpsAndOtherAdcancedStuff();
        BusInfo b = new BusInfo(station, nr, eta);
        return b;
    }

    @RequestMapping(value = "/v2/auth/token", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    AuthInfo authenticate(BindingAwareModelMap paramMap) {
        if(paramMap == null && paramMap.get("client_id") == null) {
            System.out.println("Wrong Request received");
        }

        return new AuthInfo("VF92vEWLjBbZavBkw2bVhde68AXu", "Bearer", "3599");
    }


    private int getEtaBasedOnGpsAndOtherAdcancedStuff() {
        Random rn = new Random();
        int min = 1;
        int max = 7;
        return rn.nextInt(max - min + 1) + min;
    }
}

// http://localhost:8111/bus/Central-park/201