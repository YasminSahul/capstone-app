const API = "/api";

let products = [];
let cart = [];

let currentCategory = "All";
let selectedProduct = null;
let pendingBuy = [];


/* REAL PRODUCT PHOTOS */

const productImages = {

    football:
        "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?auto=format&fit=crop&w=900&q=80",

    basketball:
        "https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&w=900&q=80",

    volleyball:
        "https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?auto=format&fit=crop&w=900&q=80",

    cricket:
        "https://images.unsplash.com/photo-1531415074968-036ba1b575da?auto=format&fit=crop&w=900&q=80",

    tshirt:
        "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80",

    shirt:
        "https://images.unsplash.com/photo-1603252109303-2751441dd157?auto=format&fit=crop&w=900&q=80",

    jeans:
        "https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80",

    dress:
        "https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=900&q=80",

    kurti:
        "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=900&q=80",

    shoe:
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80",

    slipper:
        "https://images.unsplash.com/photo-1603487742131-4160ec999306?auto=format&fit=crop&w=900&q=80",

    teddy:
        "https://images.unsplash.com/photo-1559454403-b8fb88521f11?auto=format&fit=crop&w=900&q=80",

    watch:
        "https://images.unsplash.com/photo-1524805444758-089113d48a6d?auto=format&fit=crop&w=900&q=80",

    chain:
        "https://images.unsplash.com/photo-1611652022419-a9419f74343d?auto=format&fit=crop&w=900&q=80",

    bag:
        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80",

    gift:
        "https://images.unsplash.com/photo-1513883049090-d0b7439799bf?auto=format&fit=crop&w=900&q=80",

    racket:
        "https://images.unsplash.com/photo-1622279457486-62dcc4a431d6?auto=format&fit=crop&w=900&q=80",

    dumbbell:
        "https://images.unsplash.com/photo-1517963879433-6ad2b056d5d7?auto=format&fit=crop&w=900&q=80"

};


/* SERVER REQUEST */

async function apiRequest(url, options = {}) {

    const response =
        await fetch(
            API + url,
            {
                headers: {
                    "Content-Type":
                        "application/json"
                },

                ...options
            }
        );

    const data =
        await response.json();

    if (!response.ok) {

        throw new Error(
            data.message ||
            "Server error"
        );

    }

    return data;
}


/* ESCAPE TEXT */

function escapeHTML(text) {

    return String(text ?? "")
        .replace(/[&<>"']/g, function (x) {

            return {
                "&": "&amp;",
                "<": "&lt;",
                ">": "&gt;",
                '"': "&quot;",
                "'": "&#39;"
            }[x];

        });

}


/* MONEY */

function money(value) {

    return Number(value)
        .toLocaleString("en-IN");

}


/* PAGE START */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const loginForm =
            document.getElementById(
                "loginForm"
            );

        const signinForm =
            document.getElementById(
                "signinForm"
            );

        if (loginForm) {

            loginForm.addEventListener(
                "submit",
                loginUser
            );

        }

        if (signinForm) {

            signinForm.addEventListener(
                "submit",
                registerUser
            );

        }

        if (
            document.getElementById(
                "productGrid"
            )
        ) {

            startMarketplace();

        }

    }
);


/* REGISTER */

async function registerUser(event) {

    event.preventDefault();

    const password =
        document.getElementById(
            "password"
        ).value;

    const confirmPassword =
        document.getElementById(
            "confirmPassword"
        ).value;

    if (
        password !==
        confirmPassword
    ) {

        showMessage(
            "signin-message",
            "Passwords do not match.",
            "red"
        );

        return;
    }


    try {

        await apiRequest(
            "/register",
            {
                method: "POST",

                body: JSON.stringify({

                    name:
                        document.getElementById(
                            "name"
                        ).value,

                    email:
                        document.getElementById(
                            "email"
                        ).value,

                    password:
                        password,

                    role:
                        document.getElementById(
                            "role"
                        ).value

                })
            }
        );


        showMessage(
            "signin-message",
            "Account created successfully! Redirecting to Login...",
            "green"
        );


        setTimeout(
            function () {

                location.href =
                    "caplogin.html";

            },
            1000
        );

    }

    catch (error) {

        showMessage(
            "signin-message",
            error.message,
            "red"
        );

    }

}


/* LOGIN */

async function loginUser(event) {

    event.preventDefault();


    try {

        const result =
            await apiRequest(
                "/login",
                {
                    method: "POST",

                    body: JSON.stringify({

                        email:
                            document.getElementById(
                                "loginEmail"
                            ).value,

                        password:
                            document.getElementById(
                                "loginPassword"
                            ).value,

                        role:
                            document.getElementById(
                                "loginRole"
                            ).value

                    })
                }
            );


        localStorage.setItem(
            "yashzUser",
            JSON.stringify(
                result.user
            )
        );


        /*
         * THIS IS THE IMPORTANT
         * PAGE REDIRECTION.
         */

        window.location.href =
            "capafterlogin.html";

    }

    catch (error) {

        showMessage(
            "loginMessage",
            error.message,
            "red"
        );

    }

}


/* MARKETPLACE */

async function startMarketplace() {

    const user =
        JSON.parse(
            localStorage.getItem(
                "yashzUser"
            ) || "null"
        );


    if (!user) {

        window.location.href =
            "caplogin.html";

        return;

    }


    document.getElementById(
        "userBadge"
    ).textContent =
        user.name +
        " • " +
        user.role;


    document.getElementById(
        "settingsText"
    ).textContent =
        "Logged in as " +
        user.name +
        " (" +
        user.role +
        ")";


    if (
        user.role === "SELLER"
    ) {

        document
            .getElementById(
                "sellerButton"
            )
            .classList
            .remove("hidden");

    }


    try {

        products =
            await apiRequest(
                "/products"
            );


        cart =
            JSON.parse(
                localStorage.getItem(
                    "yashzCart"
                ) || "[]"
            );


        bindMarketplace();

        renderProducts();

        updateCartCount();

    }

    catch (error) {

        document.getElementById(
            "productGrid"
        ).innerHTML =
            "<p>Unable to connect to database/server.</p>";

    }

}


/* EVENTS */

function bindMarketplace() {

    document
        .querySelectorAll(
            ".category-btn"
        )
        .forEach(
            function (button) {

                button.onclick =
                    function () {

                        document
                            .querySelectorAll(
                                ".category-btn"
                            )
                            .forEach(
                                b =>
                                    b.classList
                                        .remove(
                                            "active"
                                        )
                            );


                        button.classList.add(
                            "active"
                        );


                        currentCategory =
                            button.dataset
                                .category;


                        renderProducts();

                    };

            }
        );


    document.getElementById(
        "searchButton"
    ).onclick =
        renderProducts;


    document.getElementById(
        "searchInput"
    ).oninput =
        renderProducts;


    document.getElementById(
        "filterToggle"
    ).onclick =
        function () {

            document
                .getElementById(
                    "filterPanel"
                )
                .classList
                .toggle(
                    "hidden"
                );

        };


    [
        "deliveryFilter",
        "deliveryTimeFilter",
        "colorFilter",
        "priceFilter",
        "ratingFilter",
        "trustedFilter",
        "customFilter"
    ]
        .forEach(
            function (id) {

                document.getElementById(
                    id
                ).onchange =
                    renderProducts;

            }
        );


    document.getElementById(
        "cartButton"
    ).onclick =
        openCart;


    document.getElementById(
        "settingsButton"
    ).onclick =
        function () {

            showModal(
                "settingsModal"
            );

        };


    document.getElementById(
        "logoutButton"
    ).onclick =
        function () {

            localStorage.removeItem(
                "yashzUser"
            );

            localStorage.removeItem(
                "yashzCart"
            );

            location.href =
                "caplogin.html";

        };


    document.getElementById(
        "sellerButton"
    ).onclick =
        function () {

            showModal(
                "sellerModal"
            );

        };


    document.getElementById(
        "sellerForm"
    ).onsubmit =
        addSellerProduct;


    document.querySelectorAll(
        'input[name="payment"]'
    )
        .forEach(
            function (radio) {

                radio.onchange =
                    function () {

                        document
                            .getElementById(
                                "gpayForm"
                            )
                            .classList
                            .toggle(
                                "hidden",
                                radio.value !==
                                "gpay" ||
                                !radio.checked
                            );

                    };

            }
        );


    document.getElementById(
        "proceedPayment"
    ).onclick =
        processPayment;


    document.getElementById(
        "cartBuyButton"
    ).onclick =
        function () {

            if (!cart.length) {

                alert(
                    "Your cart is empty."
                );

                return;

            }


            pendingBuy =
                cart.slice();

            closeCart();

            showModal(
                "paymentModal"
            );

        };


    document.getElementById(
        "sendCustomRequest"
    ).onclick =
        sendCustomization;

}


/* FILTER */

function renderProducts() {

    const search =
        document
            .getElementById(
                "searchInput"
            )
            .value
            .toLowerCase()
            .trim();


    const delivery =
        document.getElementById(
            "deliveryFilter"
        ).value;


    const time =
        document.getElementById(
            "deliveryTimeFilter"
        ).value;


    const color =
        document.getElementById(
            "colorFilter"
        ).value;


    const price =
        document.getElementById(
            "priceFilter"
        ).value;


    const rating =
        document.getElementById(
            "ratingFilter"
        ).value;


    const trusted =
        document.getElementById(
            "trustedFilter"
        ).checked;


    const customizable =
        document.getElementById(
            "customFilter"
        ).checked;


    const result =
        products.filter(
            function (product) {

                const text =
                    (
                        product.name +
                        " " +
                        product.description +
                        " " +
                        product.category
                    )
                    .toLowerCase();


                if (
                    currentCategory !==
                    "All" &&
                    product.category !==
                    currentCategory
                ) {

                    return false;

                }


                if (
                    search &&
                    !text.includes(search)
                ) {

                    return false;

                }


                if (
                    delivery !== "all" &&
                    product.delivery !==
                    delivery
                ) {

                    return false;

                }


                if (
                    time !== "all" &&
                    product.deliveryTime !==
                    time
                ) {

                    return false;

                }


                if (
                    color !== "all" &&
                    product.color !== color
                ) {

                    return false;

                }


                if (
                    rating !== "all" &&
                    Number(product.rating) <
                    Number(rating)
                ) {

                    return false;

                }


                if (
                    trusted &&
                    !product.trusted
                ) {

                    return false;

                }


                if (
                    customizable &&
                    !product.customizable
                ) {

                    return false;

                }


                if (
                    price !== "all"
                ) {

                    if (
                        price === "0-500" &&
                        product.price >= 500
                    )
                        return false;


                    if (
                        price === "500-1000" &&
                        (
                            product.price < 500 ||
                            product.price > 1000
                        )
                    )
                        return false;


                    if (
                        price === "1000-2500" &&
                        (
                            product.price <= 1000 ||
                            product.price > 2500
                        )
                    )
                        return false;


                    if (
                        price === "2500-5000" &&
                        (
                            product.price <= 2500 ||
                            product.price > 5000
                        )
                    )
                        return false;


                    if (
                        price === "5000" &&
                        product.price <= 5000
                    )
                        return false;

                }


                return true;

            }
        );


    document.getElementById(
        "productCount"
    ).textContent =
        result.length +
        " products";


    document.getElementById(
        "productGrid"
    ).innerHTML =
        result.map(
            createProductCard
        ).join("");


    if (!result.length) {

        document.getElementById(
            "productGrid"
        ).innerHTML =
            "<p>No products found.</p>";

    }

}


/* PRODUCT CARD */

function createProductCard(product) {

    const image =
        productImages[
            product.type
        ] ||
        productImages.gift;


    return `

        <article class="product-card">

            <img
                class="product-image"
                src="${image}"
                alt="${escapeHTML(product.name)}"
            >

            <div class="product-info">

                <h3>
                    ${escapeHTML(product.name)}
                </h3>

                <p class="product-description">
                    ${escapeHTML(product.description)}
                </p>

                <div class="product-price">
                    ₹${money(product.price)}
                </div>

                <div class="rating">
                    ★ ${product.rating}
                    ${product.trusted
                        ? " • Yashz Trusted"
                        : ""}
                </div>

                <div class="product-actions">

                    <button
                        class="view-button"
                        onclick="viewProduct(${product.id})"
                    >
                        View
                    </button>

                    <button
                        class="add-button"
                        onclick="addToCart(${product.id})"
                    >
                        Add to Cart
                    </button>

                </div>

                <button
                    class="buy-button"
                    onclick="buyNow(${product.id})"
                >
                    Buy Now
                </button>

            </div>

        </article>

    `;

}


/* VIEW */

function viewProduct(id) {

    selectedProduct =
        products.find(
            p => p.id === id
        );


    if (!selectedProduct)
        return;


    const image =
        productImages[
            selectedProduct.type
        ] ||
        productImages.gift;


    document.getElementById(
        "productDetails"
    ).innerHTML = `

        <div class="details-layout">

            <img
                class="details-image"
                src="${image}"
                alt="${escapeHTML(
                    selectedProduct.name
                )}"
            >

            <div>

                <h2 class="details-title">
                    ${escapeHTML(
                        selectedProduct.name
                    )}
                </h2>

                <p>
                    ${escapeHTML(
                        selectedProduct.description
                    )}
                </p>

                <div class="details-price">
                    ₹${money(
                        selectedProduct.price
                    )}
                </div>

                <p>
                    ★ ${selectedProduct.rating}
                </p>

                <p>
                    Delivery:
                    ${selectedProduct.delivery}
                    •
                    ${selectedProduct.deliveryTime}
                    days
                </p>

                <div>

                    <b>Size:</b>

                    <button class="size-button">S</button>
                    <button class="size-button">M</button>
                    <button class="size-button">L</button>
                    <button class="size-button">XL</button>
                    <button class="size-button">XXL</button>

                </div>

                ${
                    selectedProduct.customizable
                    ?
                    `
                    <button
                        class="primary-button full"
                        style="margin-top:15px"
                        onclick="openCustomization(
                            ${selectedProduct.id}
                        )"
                    >
                        🎨 Customize
                    </button>
                    `
                    :
                    ""
                }

                <button
                    class="primary-button full"
                    style="margin-top:10px"
                    onclick="
                        addToCart(
                            ${selectedProduct.id}
                        );
                        closeProductModal();
                    "
                >
                    Add to Cart
                </button>

                <button
                    class="buy-button"
                    onclick="
                        buyNow(
                            ${selectedProduct.id}
                        );
                        closeProductModal();
                    "
                >
                    Buy Now
                </button>

            </div>

        </div>

    `;


    showModal(
        "productModal"
    );

}


/* CART */

function addToCart(id) {

    const product =
        products.find(
            p => p.id === id
        );


    if (!product)
        return;


    cart.push(product);


    localStorage.setItem(
        "yashzCart",
        JSON.stringify(cart)
    );


    updateCartCount();

    alert(
        product.name +
        " added to cart."
    );

}


function updateCartCount() {

    document.getElementById(
        "cartCount"
    ).textContent =
        cart.length;

}


function openCart() {

    const cartItems =
        document.getElementById(
            "cartItems"
        );


    if (!cart.length) {

        cartItems.innerHTML =
            "<p>Your cart is empty.</p>";

    }

    else {

        cartItems.innerHTML =
            cart.map(
                function (product, index) {

                    const image =
                        productImages[
                            product.type
                        ] ||
                        productImages.gift;


                    return `

                        <div class="cart-item">

                            <img
                                src="${image}"
                                alt="${escapeHTML(
                                    product.name
                                )}"
                            >

                            <div style="flex:1">

                                <b>
                                    ${escapeHTML(
                                        product.name
                                    )}
                                </b>

                                <p>
                                    ₹${money(
                                        product.price
                                    )}
                                </p>

                            </div>

                            <button
                                onclick="
                                    removeFromCart(
                                        ${index}
                                    )
                                "
                            >
                                Remove
                            </button>

                        </div>

                    `;

                }
            )
            .join("");

    }


    const total =
        cart.reduce(
            function (sum, product) {

                return sum +
                    Number(product.price);

            },
            0
        );


    document.getElementById(
        "cartTotal"
    ).textContent =
        money(total);


    showModal(
        "cartModal"
    );

}


function removeFromCart(index) {

    cart.splice(
        index,
        1
    );


    localStorage.setItem(
        "yashzCart",
        JSON.stringify(cart)
    );


    updateCartCount();

    openCart();

}


/* BUY */

function buyNow(id) {

    const product =
        products.find(
            p => p.id === id
        );


    if (!product)
        return;


    pendingBuy = [
        product
    ];


    showModal(
        "paymentModal"
    );

}


/* PAYMENT */

function processPayment() {

    const selected =
        document.querySelector(
            'input[name="payment"]:checked'
        );


    if (!selected) {

        alert(
            "Please choose payment method."
        );

        return;

    }


    if (
        selected.value ===
        "gpay"
    ) {

        const upi =
            document.getElementById(
                "upiNumber"
            ).value;

        const pin =
            document.getElementById(
                "paymentPin"
            ).value;


        if (!upi || !pin) {

            alert(
                "Enter demo UPI and PIN."
            );

            return;

        }

    }


    const total =
        pendingBuy.reduce(
            (sum, product) =>
                sum +
                Number(product.price),
            0
        );


    if (
        selected.value ===
        "cod"
    ) {

        document.getElementById(
            "successTitle"
        ).textContent =
            "Order placed successfully!";


        document.getElementById(
            "successMessage"
        ).textContent =
            "Your order of ₹" +
            money(total) +
            " will be delivered in 3–5 days. Thank you for shopping with Yashz Mart!";

    }

    else {

        document.getElementById(
            "successTitle"
        ).textContent =
            "Demo payment successful!";


        document.getElementById(
            "successMessage"
        ).textContent =
            "₹" +
            money(total) +
            " demo payment completed. No real money was charged.";

    }


    cart = [];

    localStorage.setItem(
        "yashzCart",
        "[]"
    );


    updateCartCount();

    closePayment();

    showModal(
        "successModal"
    );

}


/* CUSTOMIZATION */

function openCustomization(id) {

    selectedProduct =
        products.find(
            p => p.id === id
        );


    if (!selectedProduct)
        return;


    document.getElementById(
        "customProductName"
    ).textContent =
        selectedProduct.name;


    document.getElementById(
        "customRequest"
    ).value = "";


    showModal(
        "customModal"
    );

}


function sendCustomization() {

    const request =
        document.getElementById(
            "customRequest"
        ).value
        .trim();


    if (!request) {

        alert(
            "Enter your customization request."
        );

        return;

    }


    closeCustomModal();


    document.getElementById(
        "successTitle"
    ).textContent =
        "Customization request sent";


    document.getElementById(
        "successMessage"
    ).textContent =
        "Your request was sent to the seller. The seller can review your customization request.";


    showModal(
        "successModal"
    );

}


/* SELLER */

async function addSellerProduct(event) {

    event.preventDefault();


    const user =
        JSON.parse(
            localStorage.getItem(
                "yashzUser"
            )
        );


    try {

        const product =
            await apiRequest(
                "/products",
                {
                    method: "POST",

                    body: JSON.stringify({

                        sellerId:
                            user.id,

                        name:
                            document.getElementById(
                                "sellerProductName"
                            ).value,

                        category:
                            document.getElementById(
                                "sellerCategory"
                            ).value,

                        type:
                            document.getElementById(
                                "sellerType"
                            ).value,

                        price:
                            Number(
                                document.getElementById(
                                    "sellerPrice"
                                ).value
                            ),

                        color:
                            document.getElementById(
                                "sellerColor"
                            ).value,

                        description:
                            document.getElementById(
                                "sellerDescription"
                            ).value,

                        customizable:
                            document.getElementById(
                                "sellerCustom"
                            ).checked

                    })
                }
            );


        products.unshift(
            product
        );


        document.getElementById(
            "sellerForm"
        ).reset();


        showMessage(
            "sellerMessage",
            "Product added successfully!",
            "green"
        );


        renderProducts();

    }

    catch (error) {

        showMessage(
            "sellerMessage",
            error.message,
            "red"
        );

    }

}


/* MODAL FUNCTIONS */

function showModal(id) {

    document
        .getElementById(id)
        .classList
        .remove("hidden");

}


function hideModal(id) {

    document
        .getElementById(id)
        .classList
        .add("hidden");

}


function closeProductModal() {
    hideModal("productModal");
}

function closeCart() {
    hideModal("cartModal");
}

function closePayment() {
    hideModal("paymentModal");
}

function closeSuccess() {
    hideModal("successModal");
}

function closeCustomModal() {
    hideModal("customModal");
}

function closeSettings() {
    hideModal("settingsModal");
}

function closeSeller() {
    hideModal("sellerModal");
}


/* MESSAGE */

function showMessage(
    id,
    text,
    color
) {

    const element =
        document.getElementById(id);


    if (!element)
        return;


    element.textContent =
        text;

    element.style.color =
        color;

}