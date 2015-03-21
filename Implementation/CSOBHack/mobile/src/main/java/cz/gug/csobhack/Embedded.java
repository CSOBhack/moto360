package cz.gug.csobhack;

/**
 * Created by Jakub on 21.03.15.
 */
public class Embedded {
    News[] _embedded;

    public News[] get_embedded() {
        return _embedded;
    }

    public void set_embedded(News[] _embedded) {
        this._embedded = _embedded;
    }
}
