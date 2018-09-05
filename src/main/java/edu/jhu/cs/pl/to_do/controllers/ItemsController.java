package edu.jhu.cs.pl.to_do.controllers;

import edu.jhu.cs.pl.to_do.Server;
import edu.jhu.cs.pl.to_do.models.Item;
import edu.jhu.cs.pl.to_do.repositories.ItemsRepository;
import io.javalin.BadRequestResponse;
import io.javalin.Context;
import io.javalin.NotFoundResponse;

import java.io.IOException;
import java.sql.SQLException;

public class ItemsController {
    public static void newItem(Context ctx) throws SQLException {
        Server.getItemsRepository().create(new Item());
        ctx.status(201);
    }

    public static void getItems(Context ctx) throws SQLException {
        ctx.json(Server.getItemsRepository().getItems());
    }

    public static void editItemDescription(Context ctx) throws IOException, SQLException, ItemsRepository.NonExistingItemException {
        var item = getItem(ctx);
        var itemParameter = Server.getJson().readTree(ctx.body());
        if (itemParameter == null || itemParameter.size() != 1 ||
                ! itemParameter.hasNonNull("description") ||
                ! itemParameter.get("description").isTextual())
            throw new BadRequestResponse();
        item.setDescription(itemParameter.get("description").asText());
        Server.getItemsRepository().update(item);
        ctx.status(204);
    }

    public static void markItemAsDone(Context ctx) throws ItemsRepository.NonExistingItemException, SQLException {
        Server.getItemsRepository().deleteItem(getItem(ctx));
        ctx.status(204);
    }

    private static Item getItem(Context ctx) throws ItemsRepository.NonExistingItemException, SQLException {
        int itemIdentifier;
        try {
            itemIdentifier = Integer.parseInt(ctx.pathParam("item-identifier"));
        } catch (NumberFormatException e) {
            throw new NotFoundResponse();
        }
        return Server.getItemsRepository().getItem(itemIdentifier);
    }
}
