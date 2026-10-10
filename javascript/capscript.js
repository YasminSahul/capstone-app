console.log("Yashz Mart JavaScript loaded");


/* ==============================
   LOGIN
   ============================== */

const loginForm =
    document.getElementById("loginForm");

if (loginForm) {

    loginForm.addEventListener(
        "submit",
        function(event) {

            event.preventDefault();

            const email =
                document.getElementById(
                    "loginEmail"
                ).value.trim();

            const password =
                document.getElementById(
                    "loginPassword"
                ).value.trim();

            const message =
                document.getElementById(
                    "loginMessage"
                );

            if (email === "" ||
                password === "") {

                message.textContent =
                    "Please enter email and password.";

                message.style.color = "red";

                return;
            }

            /*
             * Level 1 demonstration only.
             *
             * Real Java + database login
             * will be added in a later level.
             */

            localStorage.setItem(
                "yashzmartUser",
                JSON.stringify({
                    email: email
                })
            );

            message.textContent =
                "Login successful.";

            message.style.color = "green";

            setTimeout(function() {

                window.location.href =
                    "capafterlogin.html";

            }, 500);

        }
    );
}


/* ==============================
   SIGN UP
   ============================== */

const signupForm =
    document.getElementById("signupForm");

if (signupForm) {

    signupForm.addEventListener(
        "submit",
        function(event) {

            event.preventDefault();

            const name =
                document.getElementById(
                    "signupName"
                ).value.trim();

            const email =
                document.getElementById(
                    "signupEmail"
                ).value.trim();

            const password =
                document.getElementById(
                    "signupPassword"
                ).value.trim();

            const role =
                document.getElementById(
                    "signupRole"
                ).value;

            const message =
                document.getElementById(
                    "signupMessage"
                );

            if (!name ||
                !email ||
                !password) {

                message.textContent =
                    "Please fill all fields.";

                message.style.color = "red";

                return;
            }

            const user = {

                name: name,

                email: email,

                password: password,

                role: role

            };

            localStorage.setItem(
                "yashzmartAccount",
                JSON.stringify(user)
            );

            message.textContent =
                "Account created successfully.";

            message.style.color = "green";

            setTimeout(function() {

                window.location.href =
                    "caplogin.html";

            }, 700);

        }
    );
}


/* ==============================
   MARKETPLACE
   ============================== */

const searchButton =
    document.getElementById(
        "searchButton"
    );

if (searchButton) {

    searchButton.addEventListener(
        "click",
        function() {

            const searchInput =
                document.getElementById(
                    "searchInput"
                );

            const value =
                searchInput.value.trim();

            if (value === "") {

                alert(
                    "Please enter a product name."
                );

                return;
            }

            alert(
                "Search module will be added in Level 5."
            );

        }
    );
}


/* ==============================
   CART
   ============================== */

const cartButton =
    document.getElementById(
        "cartButton"
    );

if (cartButton) {

    cartButton.addEventListener(
        "click",
        function() {

            alert(
                "Cart module will be added in a later level."
            );

        }
    );
}


/* ==============================
   SETTINGS
   ============================== */

const settingsButton =
    document.getElementById(
        "settingsButton"
    );

if (settingsButton) {

    settingsButton.addEventListener(
        "click",
        function() {

            alert(
                "Settings module will be added later."
            );

        }
    );
}