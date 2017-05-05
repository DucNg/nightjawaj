package modele;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import tools.Server;

import java.util.*;

import static spark.Spark.post;
import static spark.Spark.get;

/**
 * Created by dnguye2 on 27/03/17.
 */
public class Event {
    private int ide;
    private String name;
    private String dateDeb;
    private String dateFin;

    private List<User> hisAmbiances;
    private List<Activity> hisActivities;

    public Event(String name, String dateDeb, String dateFin) {
        this.name = name;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
    }

    public Event(int ide,String name, String dateDeb, String dateFin) {
        this.ide = ide;
        this.name = name;
        this.dateDeb = dateDeb;
        this.dateFin = dateFin;
    }

    public static void start(){
        post("/event", (request, response) -> {
            Event e = new Event(request.queryParams("nomevenement"),
                    request.queryParams("heuredebut"),
                    request.queryParams("heurefin"));

            if (!e.dateDeb.matches("([0-2][0-9]{3})-([0-1][0-9])-([0-3][0-9])T([0-5][0-9]):([0-5][0-9])")){
                response.redirect("/error?msg=Error, start hour doesn't match format. Hint : ([0-2][0-9]{3})-([0-1][0-9])-([0-3][0-9])T([0-5][0-9]):([0-5][0-9])");
            }
            else if (!e.dateDeb.matches("([0-2][0-9]{3})-([0-1][0-9])-([0-3][0-9])T([0-5][0-9]):([0-5][0-9])")){
                response.redirect("/error?msg=Error, end hour doesn't match format. Hint : ([0-2][0-9]{3})-([0-1][0-9])-([0-3][0-9])T([0-5][0-9]):([0-5][0-9])");
            }
            User u = request.session().attribute("user");
            if (u == null ) response.redirect("/");
            Server.getDatabase().createEvent(u.getId(),e.name,e.dateDeb,e.dateFin); // Adding event to database
            Map map = new HashMap();
            map.put("eventname", e.name);
            map.put("nom", u.getName());
            map.put("prenom", u.getFirstname());
            map.put("hdeb",e.dateDeb);
            map.put("hfin",e.dateFin);

            //Manage user address
            Address a = Address.getAddressFromId(u.getPlaceid());
            map.put("adresse",a.formattedAddress);

            return new ModelAndView(map, "listeevenement.hbs");
        }, new HandlebarsTemplateEngine());
    }
}
