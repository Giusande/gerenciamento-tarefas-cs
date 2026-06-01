const API_URL = "http://localhost:8080";

const loginTab = document.getElementById("loginTab");
const registerTab = document.getElementById("registerTab");

const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");

loginTab.addEventListener("click", () => {
  loginTab.classList.add("active");
  registerTab.classList.remove("active");

  loginForm.classList.remove("hidden-form");
  loginForm.classList.add("active-form");

  registerForm.classList.add("hidden-form");
  registerForm.classList.remove("active-form");
});

registerTab.addEventListener("click", () => {
  registerTab.classList.add("active");
  loginTab.classList.remove("active");

  registerForm.classList.remove("hidden-form");
  registerForm.classList.add("active-form");

  loginForm.classList.add("hidden-form");
  loginForm.classList.remove("active-form");
});

// CADASTRO

registerForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const nome = document.getElementById("registerNome").value;
  const email = document.getElementById("registerEmail").value;
  const senha = document.getElementById("registerSenha").value;

  const message = document.getElementById("registerMessage");

  try {
    const response = await fetch(`${API_URL}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        nome,
        email,
        senha,
      }),
    });

    if (!response.ok) {
      throw new Error("Erro ao cadastrar usuário");
    }

    message.innerText = "Conta criada com sucesso!";
    message.classList.add("success");

    registerForm.reset();
  } catch (error) {
    message.innerText = error.message;
    message.classList.add("error");
  }
});

// LOGIN

loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const email = document.getElementById("loginEmail").value;
  const senha = document.getElementById("loginSenha").value;

  const message = document.getElementById("loginMessage");

  try {
    const response = await fetch(`${API_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email,
        senha,
      }),
    });

    if (!response.ok) {
      throw new Error("Email ou senha inválidos");
    }

    const data = await response.json();

    localStorage.setItem("token", data.token);

    message.innerText = "Login realizado com sucesso!";
    message.classList.add("success");

    setTimeout(() => {
      window.location.href = "dashboard.html";
    }, 1500);
  } catch (error) {
    message.innerText = error.message;
    message.classList.add("error");
  }
});
