package cz.gug.csobhack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Jakub on 21.03.15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Embedded {
    Wrapper _embedded;

    public Wrapper get_embedded() {
        return _embedded;
    }

    public void set_embedded(Wrapper _embedded) {
        this._embedded = _embedded;
    }
}
