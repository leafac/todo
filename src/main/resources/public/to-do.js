let previousList = [];
let currentList = [];

window.addEventListener("load", () => poll());

const poll = async () => {
    previousList = currentList;
    currentList = await (await fetch("/items")).json();
    render();
    await sleep(200);
    poll();
};

const render = () => {
    const list = document.getElementById("list");
    const newItemDom = item => {
        const itemDom = document.createElement("li");
        itemDom.id = item.id;
        const checkbox = document.createElement("input");
        checkbox.setAttribute("type", "checkbox");
        checkbox.addEventListener("change", removeItemAnimation(itemDom));
        checkbox.addEventListener("change", removeItem(item));
        itemDom.appendChild(checkbox);
        const text = document.createElement("input");
        text.setAttribute("type", "text");
        text.value = item.description;
        text.addEventListener("input", updateItem(item, text));
        itemDom.appendChild(text);
        return itemDom;
    };

    const removeItemAnimation = itemDom => async () => {
        itemDom.style.marginLeft = "100px";
        itemDom.style.opacity = 0;
        await sleep(300);
        itemDom.remove();
    };

    // Work through ‘previousList’ and ‘currentList’ in sync, finding the differences.
    for (let previousListIndex = 0, currentListIndex = 0; previousListIndex < previousList.length && currentListIndex < currentList.length; ) {
        const previousListItem = previousList[previousListIndex];
        const currentListItem = currentList[currentListIndex];
        // Same item.
        if (previousListItem.id === currentListItem.id) {
            const inputText = document.getElementById(currentListItem.id).querySelector("input[type=\"text\"]");
            if (previousListItem.description !== currentListItem.description && inputText !== document.activeElement) {
                inputText.value = currentListItem.description;
            }
            previousListIndex++;
            currentListIndex++;
        }
        // Item removed.
        else if (previousListItem.id < currentListItem.id) {
            removeItemAnimation(document.getElementById(previousListItem.id))();
            previousListIndex++;
        }
        // Item added.
        else if (previousListItem.id > currentListItem.id) {
            list.insertBefore(document.getElementById(previousListItem.id), newItemDom(currentListItem));
            currentListIndex++;
        }
    }

    // Add remaining items to the end.
    for (let currentListIndex = previousList.length; currentListIndex < currentList.length; currentListIndex++) {
        list.appendChild(newItemDom(currentList[currentListIndex]));
    }

    // Remove remaining items at the end.
    for (let previousListIndex = currentList.length; previousListIndex < currentList.length; previousListIndex++) {
        document.getElementById(currentList[previousListIndex].id).remove();
    }
};

const newItem = () => fetch("/items", { method: "POST" });

const removeItem = item => () => fetch(`/items/${item.id}`, { method: "DELETE" });

const updateItem = (item, text) => () => fetch(`/items/${item.id}`, { method: "PUT", body: JSON.stringify({ description: text.value}) });

const sleep = duration => new Promise(resolve => window.setTimeout(resolve, duration));
