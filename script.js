/* ========================================= */
/* LOGIN SYSTEM */
/* ========================================= */

function login() {

    let email =
        document.getElementById("email").value.trim();

    let password =
        document.getElementById("password").value.trim();

    let message =
        document.getElementById("message");


    /* Check empty fields */

    if (email === "" || password === "") {

        message.textContent =
            "Please enter email and password.";

        message.style.color = "red";

        return;
    }


    /* Get saved account */

    let savedEmail =
        localStorage.getItem("email");

    let savedPassword =
        localStorage.getItem("password");


    /* Default account */

    if (savedEmail === null) {

        savedEmail =
            "yasminss2107@gmail.com";

        savedPassword =
            "12345";
    }


    /* Check login */

    if (
        email === savedEmail &&
        password === savedPassword
    ) {

        message.textContent =
            "Login Successful!";

        message.style.color =
            "green";


        setTimeout(function () {

            document.getElementById(
                "loginPage"
            ).style.display = "none";


            document.getElementById(
                "productPage"
            ).style.display = "block";

        }, 800);

    } else {

        message.textContent =
            "Invalid email or password.";

        message.style.color =
            "red";
    }
}


/* ========================================= */
/* SIGN IN */
/* ========================================= */

function signIn() {

    let newEmail =
        prompt("Enter your email ID:");


    if (
        newEmail === null ||
        newEmail.trim() === ""
    ) {

        return;
    }


    let newPassword =
        prompt("Create your password:");


    if (
        newPassword === null ||
        newPassword.trim() === ""
    ) {

        return;
    }


    localStorage.setItem(
        "email",
        newEmail.trim()
    );


    localStorage.setItem(
        "password",
        newPassword
    );


    let message =
        document.getElementById("message");


    message.textContent =
        "Account created successfully!";

    message.style.color =
        "green";


    document.getElementById(
        "email"
    ).value = newEmail;

}


/* ========================================= */
/* CANCEL LOGIN */
/* ========================================= */

function cancelLogin() {

    document.getElementById(
        "email"
    ).value = "";


    document.getElementById(
        "password"
    ).value = "";


    document.getElementById(
        "message"
    ).textContent = "";

}


/* ========================================= */
/* CART */
/* ========================================= */

let cart = [];


/* ========================================= */
/* ADD TO CART */
/* ========================================= */

function addToCart(productName, price) {

    let existingProduct =
        cart.find(function (item) {

            return item.name === productName;

        });


    if (existingProduct) {

        existingProduct.quantity++;

    } else {

        cart.push({

            name: productName,

            price: price,

            quantity: 1

        });

    }


    displayCart();


    alert(
        productName +
        " added to cart!"
    );
}


/* ========================================= */
/* DISPLAY CART */
/* ========================================= */

function displayCart() {

    let cartItems =
        document.getElementById(
            "cart-items"
        );


    let cartTotal =
        document.getElementById(
            "cart-total"
        );


    cartItems.innerHTML = "";


    /* Empty cart */

    if (cart.length === 0) {

        cartItems.innerHTML =
            "<p>Your cart is empty.</p>";

        cartTotal.textContent =
            "Total: ₹0";

        return;
    }


    let total = 0;


    cart.forEach(function (item, index) {

        let itemTotal =
            item.price * item.quantity;


        total += itemTotal;


        let cartItem =
            document.createElement("div");


        cartItem.className =
            "cart-item";


        cartItem.innerHTML = `

            <span>

                <strong>
                    ${item.name}
                </strong>

                <br>

                ₹${item.price}
                ×
                ${item.quantity}

                =
                ₹${itemTotal}

            </span>

            <button
                onclick="removeFromCart(${index})">
                Remove
            </button>

        `;


        cartItems.appendChild(
            cartItem
        );

    });


    cartTotal.textContent =
        "Total: ₹" + total;
}


/* ========================================= */
/* REMOVE FROM CART */
/* ========================================= */

function removeFromCart(index) {

    cart.splice(index, 1);

    displayCart();

}


/* ========================================= */
/* BUY NOW - SINGLE PRODUCT */
/* ========================================= */

function buyNow(productName, price) {

    let confirmation =
        confirm(
            "Do you want to buy " +
            productName +
            " for ₹" +
            price +
            "?"
        );


    if (confirmation) {

        alert(
            "✅ " +
            productName +
            " purchased successfully!"
        );

    } else {

        alert(
            "Purchase cancelled."
        );

    }

}


/* ========================================= */
/* BUY CART */
/* ========================================= */

function buyCart() {

    if (cart.length === 0) {

        alert(
            "Your cart is empty."
        );

        return;
    }


    let total = 0;


    cart.forEach(function (item) {

        total +=
            item.price *
            item.quantity;

    });


    let confirmation =
        confirm(
            "Do you want to buy all items in your cart?\n\n" +
            "Total Amount: ₹" +
            total
        );


    if (confirmation) {

        alert(
            "✅ Purchase successful!\n\n" +
            "Total Paid: ₹" +
            total
        );


        /* Empty cart after purchase */

        cart = [];

        displayCart();

    } else {

        alert(
            "Purchase cancelled."
        );

    }

}


/* ========================================= */
/* LOGOUT */
/* ========================================= */

function logout() {

    let confirmation =
        confirm(
            "Do you want to logout?"
        );


    if (confirmation) {

        document.getElementById(
            "productPage"
        ).style.display = "none";


        document.getElementById(
            "loginPage"
        ).style.display = "flex";


        document.getElementById(
            "email"
        ).value = "";


        document.getElementById(
            "password"
        ).value = "";


        document.getElementById(
            "message"
        ).textContent = "";


        cart = [];

        displayCart();

    }

}
