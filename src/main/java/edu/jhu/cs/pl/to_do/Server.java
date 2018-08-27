package edu.jhu.cs.pl.to_do;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.jhu.cs.pl.to_do.controllers.ItemsController;
import edu.jhu.cs.pl.to_do.repositories.ItemsRepository;
import io.javalin.Javalin;
import io.javalin.JavalinEvent;
import io.javalin.staticfiles.Location;
import org.postgresql.ds.PGSimpleDataSource;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Server {
    private static DataSource database;
    private static ItemsRepository itemsRepository;
    private static ObjectMapper json = new ObjectMapper();

    public static void main(String[] args) throws SQLException {
        Javalin.create()
                .routes(() -> {
                    path("items", () -> {
                        post(ItemsController::newItem);
                        get(ItemsController::getItems);
                        path(":item-identifier", () -> {
                            put(ItemsController::editItemDescription);
                            delete(ItemsController::markItemAsDone);
                        });
                    });
                })

                .exception(ItemsRepository.NonExistingItemException.class, (e, ctx) -> ctx.status(404))
                .exception(JsonProcessingException.class, (e, ctx) -> ctx.status(400))

                .enableStaticFiles("/public")
                .enableStaticFiles(System.getProperty("user.dir") + "/src/main/resources/public", Location.EXTERNAL)

                .event(JavalinEvent.SERVER_STARTING, () -> {
                    if (System.getenv("JDBC_DATABASE_URL") != null) {
                        var postgresDatabase = new PGSimpleDataSource();
                        postgresDatabase.setURL(System.getenv("JDBC_DATABASE_URL"));
                        database = postgresDatabase;
                    }
                    else {
                        var sqliteDatabase = new SQLiteDataSource();
                        sqliteDatabase.setUrl("jdbc:sqlite:to-do.db");
                        database = sqliteDatabase;
                    }
                    itemsRepository = new ItemsRepository(database);
                })

                .start(System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 7000);
    }

    public static ItemsRepository getItemsRepository() {
        return itemsRepository;
    }

    public static ObjectMapper getJson() {
        return json;
    }
}
