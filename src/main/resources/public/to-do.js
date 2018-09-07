window.addEventListener("load", () => { poll(); });

const poll = async () => {
    render(await (await fetch("/items")).json());
    await sleep(200);
    poll();
};

const newItem = () => { fetch("/items", {method: "POST"}); };

const removeItem = item => { fetch(`/items/${item.id}`, { method: "DELETE" }) };

const updateItem = item => { fetch(`/items/${item.id}`, { method: "PUT", body: JSON.stringify({ description: item.description}) }) };

const render = items => {
    const itemsDom = document.getElementById("items");

    const newItemDom = item => {
        const itemDom = document.createElement("li");
        itemDom.id = item.id;
        const checkbox = document.createElement("input");
        checkbox.setAttribute("type", "checkbox");
        checkbox.addEventListener("change", () => { removeItem(item); });
        itemDom.appendChild(checkbox);
        const text = document.createElement("input");
        text.setAttribute("type", "text");
        text.value = item.description;
        text.addEventListener("input", () => {
            item.description = text.value;
            updateItem(item);
        });
        itemDom.appendChild(text);
        return itemDom;
    };

    const removeItemDom = async itemDom => {
        itemDom.style.marginLeft = "100px";
        itemDom.style.opacity = 0;
        await sleep(300);
        itemDom.remove();
    };

    const step = (items, itemsDoms) => {
        if (itemsDoms.length === 0) return items.forEach(item => { itemsDom.append(newItemDom(item)); });
        if (items.length === 0) return itemsDoms.forEach(removeItemDom);
        const [item, ...itemsRest] = items;
        const [itemDom, ...itemsDomsRest] = itemsDoms;
        if (item.id == itemDom.id) {
            const text = itemDom.querySelector("input[type=\"text\"]");
            if (item.description !== text.value && text !== document.activeElement) text.value = item.description;
            step(itemsRest, itemsDomsRest);
        }
        else if (item.id > itemDom.id) {
            removeItemDom(itemDom);
            step(items, itemsDomsRest);
        }
        else if (item.id < itemDom.id) {
            itemDom.before(newItemDom(item));
            step(itemsRest, itemsDoms);
        }
    };

    step(items, Array.from(itemsDom.children));
};

const sleep = duration => new Promise(resolve => { window.setTimeout(resolve, duration); });
