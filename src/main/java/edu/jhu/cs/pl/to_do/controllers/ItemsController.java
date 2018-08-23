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
    public static void getItems(Context ctx) throws SQLException {
        ctx.json(Server.getItemsRepository().getItems());
    }

    public static void newItem(Context ctx) throws SQLException, IOException {
        var itemParameter = Server.getJson().readTree(ctx.body());
        if (itemParameter == null || itemParameter.size() != 1 ||
                ! itemParameter.hasNonNull("description") ||
                ! itemParameter.get("description").isTextual())
            throw new BadRequestResponse();
        var item = new Item(itemParameter.get("description").asText());
        Server.getItemsRepository().save(item);
        ctx.status(201);
        ctx.result(Integer.toString(item.getId()));
    }

    public static void editItemDescription(Context ctx) throws IOException, SQLException, ItemsRepository.NonExistingItemException {
        var itemParameter = Server.getJson().readTree(ctx.body());
        if (itemParameter == null || itemParameter.size() != 1 ||
                ! itemParameter.hasNonNull("description") ||
                ! itemParameter.get("description").isTextual())
            throw new BadRequestResponse();
        int itemIdentifier;
        try {
            itemIdentifier = Integer.parseInt(ctx.pathParam("item-identifier"));
        } catch (NumberFormatException e) {
            throw new NotFoundResponse();
        }
        var item = Server.getItemsRepository().getItem(itemIdentifier);
        item.setDescription(itemParameter.get("description").asText());
        Server.getItemsRepository().save(item);
        ctx.status(204);
    }

    public static void markItemAsDone(Context ctx) throws ItemsRepository.NonExistingItemException, SQLException {
        int itemIdentifier;
        try {
            itemIdentifier = Integer.parseInt(ctx.pathParam("item-identifier"));
        } catch (NumberFormatException e) {
            throw new NotFoundResponse();
        }
        Server.getItemsRepository().deleteItem(itemIdentifier);
        ctx.status(204);
    }
}
