const API_URL = "http://localhost:8080";
const token = localStorage.getItem("token");

if (!token) {
  window.location.href = "index.html";
}

const projectsGrid = document.getElementById("projectsGrid");

const modal = document.getElementById("projectModal");
const openModalBtn = document.getElementById("openModalBtn");
const closeModalBtn = document.getElementById("closeModalBtn");

const projectForm = document.getElementById("projectForm");

openModalBtn.addEventListener("click", () => {
  modal.style.display = "flex";
});

closeModalBtn.addEventListener("click", () => {
  modal.style.display = "none";
});

window.addEventListener("click", (e) => {
  if (e.target === modal) {
    modal.style.display = "none";
  }
});

async function carregarProjetos() {
  try {
    const response = await fetch(`${API_URL}/projetos`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const projetos = await response.json();

    projectsGrid.innerHTML = "";

    projetos.content?.forEach((projeto) => {
      const card = document.createElement("div");
      card.classList.add("project-card");

      card.innerHTML = `
            <h3>${projeto.nome}</h3>

            <div class="project-footer">
              <span>${new Date().toLocaleDateString()}</span>
              <span class="status">Ativo</span>
            </div>
          `;

      card.addEventListener("click", () => {
        localStorage.setItem("projetoId", projeto.id);
        window.location.href = "projetos.html";
      });

      projectsGrid.appendChild(card);
    });
  } catch (error) {
    console.error(error);
  }
}

projectForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const nome = document.getElementById("projectNome").value;

  try {
    const response = await fetch(`${API_URL}/projetos`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        nome,
      }),
    });

    if (!response.ok) {
      throw new Error("Erro ao criar projeto");
    }

    modal.style.display = "none";

    projectForm.reset();

    carregarProjetos();
  } catch (error) {
    alert(error.message);
  }
});

document.getElementById("logoutBtn").addEventListener("click", () => {
  localStorage.removeItem("token");
  localStorage.removeItem("projetoId");

  window.location.href = "index.html";
});

carregarProjetos();
