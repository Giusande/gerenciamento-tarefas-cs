const API_URL = "http://localhost:8080";

const token = localStorage.getItem("token");
const projetoId = localStorage.getItem("projetoId");

let tarefaAtual = null;
let tarefaBloqueadaId = null;
let tarefaExcluirId = null;

if (!token || !projetoId) {
  window.location.href = "index.html";
}

const taskModal = document.getElementById("taskModal");

document.getElementById("openTaskModalBtn").addEventListener("click", () => {
  taskModal.style.display = "flex";
});

document.getElementById("closeTaskModalBtn").addEventListener("click", () => {
  taskModal.style.display = "none";
});

document.getElementById("restrita").addEventListener("change", (e) => {
  document.getElementById("senhaGroup").style.display = e.target.checked
    ? "block"
    : "none";
});

async function carregarProjeto() {
  try {
    const response = await fetch(`${API_URL}/projetos/${projetoId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const projeto = await response.json();

    document.getElementById("projectName").innerText = projeto.nome;
  } catch (error) {
    console.error(error);
  }
}

async function carregarTarefas() {
  try {
    const response = await fetch(`${API_URL}/tarefas/projeto/${projetoId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const data = await response.json();

    const tarefas = data.content || [];

    const todoColumn = document.getElementById("todoColumn");
    const doingColumn = document.getElementById("doingColumn");
    const doneColumn = document.getElementById("doneColumn");

    todoColumn.innerHTML = "";
    doingColumn.innerHTML = "";
    doneColumn.innerHTML = "";

    let todo = 0;
    let doing = 0;
    let done = 0;

    tarefas.forEach((tarefa) => {
      const card = document.createElement("div");
      card.classList.add("task-card");

      card.draggable = true;
      card.dataset.id = tarefa.id;

      card.addEventListener("dragstart", () => {
        card.classList.add("dragging");
      });

      card.addEventListener("dragend", () => {
        card.classList.remove("dragging");
      });

      const prioridadeClass = tarefa.prioridade.toLowerCase();

      card.innerHTML = `
            <div class="task-top">
              <span class="priority ${prioridadeClass}">
                ${tarefa.prioridade}
              </span>

              ${
                tarefa.bloqueada
                  ? '<span class="restricted">🔒 Restrita</span>'
                  : ""
              }
              </div>

              <div class="tags">
  
                ${
                  tarefa.etiquetas
                    ? tarefa.etiquetas
                        .map(
                          (etiqueta) => `
                            <span
                              class="tag"
                              style="
                                background:${etiqueta.cor}20;
                                color:${etiqueta.cor};
                                border:1px solid ${etiqueta.cor}40;
                              "
                            >
                              ${etiqueta.nome}
                            </span>
                          `,
                        )
                        .join("")
                    : ""
                }
  
              </div>

            ${
              tarefa.bloqueada
                ? `
                <h3>🔒 Tarefa Restrita</h3>

                <p>
                  Conteúdo protegido por senha
                </p>

                <button
                  class="unlock-btn"
                  data-id="${tarefa.id}"
                >
                  Desbloquear
                </button>
              `
                : `
                <h3>${tarefa.titulo}</h3>

                <p>${tarefa.descricao}</p>

              `
            }
            <div class="task-actions">

              <button
                class="action-btn edit-btn"
                data-id="${tarefa.id}"
              >
                Editar
              </button>

              <button
                class="action-btn delete-btn"
                data-id="${tarefa.id}"
              >
                Excluir
              </button>

            </div>
            `;

      const deleteBtn = card.querySelector(".delete-btn");

      if (deleteBtn) {
        deleteBtn.addEventListener("click", () => confirmarExclusao(tarefa.id));
      }

      const unlockBtn = card.querySelector(".unlock-btn");

      if (unlockBtn) {
        unlockBtn.addEventListener("click", () => {
          tarefaBloqueadaId = tarefa.id;

          document.getElementById("unlockModal").style.display = "flex";
        });
      }

      card.addEventListener("dblclick", () => {
        abrirDetalhes(tarefa);
      });

      const editBtn = card.querySelector(".edit-btn");

      editBtn.addEventListener("click", () => {
        abrirModalEdicao(tarefa);
      });

      if (tarefa.status === "PENDENTE") {
        todoColumn.appendChild(card);
        todo++;
      }

      if (tarefa.status === "EM_ANDAMENTO") {
        doingColumn.appendChild(card);
        doing++;
      }

      if (tarefa.status === "CONCLUIDA") {
        doneColumn.appendChild(card);
        done++;
      }
    });

    document.getElementById("todoCount").innerText = todo;
    document.getElementById("doingCount").innerText = doing;
    document.getElementById("doneCount").innerText = done;
  } catch (error) {
    console.error(error);
  }
}

document.getElementById("taskForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const titulo = document.getElementById("titulo").value;
  const descricao = document.getElementById("descricao").value;
  const prioridade = document.getElementById("prioridade").value;
  const status = document.getElementById("status").value;
  const restrita = document.getElementById("restrita").checked;
  const senha = document.getElementById("senha").value;
  const etiquetasIds = [
    ...document.querySelectorAll("#etiquetasContainer input:checked"),
  ].map((checkbox) => Number(checkbox.value));

  try {
    const response = await fetch(`${API_URL}/tarefas`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        titulo,
        descricao,
        prioridade,
        status,
        restrita,
        senha,
        projetoId,
        etiquetasIds,
      }),
    });

    if (!response.ok) {
      throw new Error("Erro ao criar tarefa");
    }

    taskModal.style.display = "none";

    document.getElementById("taskForm").reset();

    carregarTarefas();
  } catch (error) {
    alert(error.message);
  }
});

function configurarDragAndDrop() {
  const columns = document.querySelectorAll(".tasks-container");

  columns.forEach((column) => {
    column.addEventListener("dragover", (e) => {
      e.preventDefault();
      column.classList.add("drag-over");
    });

    column.addEventListener("dragleave", () => {
      column.classList.remove("drag-over");
    });

    column.addEventListener("drop", async (e) => {
      e.preventDefault();

      column.classList.remove("drag-over");

      const card = document.querySelector(".dragging");

      if (!card) return;

      const tarefaId = card.dataset.id;

      const novoStatus = column.dataset.status;

      try {
        const response = await fetch(`${API_URL}/tarefas/${tarefaId}`, {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            status: novoStatus,
          }),
        });

        if (!response.ok) {
          throw new Error("Erro ao atualizar status");
        }

        carregarTarefas();
      } catch (error) {
        console.error(error);
        alert("Erro ao mover tarefa");
      }
    });
  });
}

function abrirModalEdicao(tarefa) {
  document.getElementById("editId").value = tarefa.id;

  document.getElementById("editTitulo").value = tarefa.titulo;

  document.getElementById("editDescricao").value = tarefa.descricao;

  document.getElementById("editPrioridade").value = tarefa.prioridade;

  document.getElementById("editStatus").value = tarefa.status;

  document.getElementById("editTaskModal").style.display = "flex";
}

function fecharModalEdicao() {
  document.getElementById("editTaskModal").style.display = "none";
}

document
  .getElementById("editTaskForm")
  .addEventListener("submit", async (e) => {
    e.preventDefault();

    const id = document.getElementById("editId").value;
    const etiquetasIds = [
      ...document.querySelectorAll("#editEtiquetasContainer input:checked"),
    ].map((checkbox) => Number(checkbox.value));

    try {
      const response = await fetch(`${API_URL}/tarefas/${id}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          titulo: document.getElementById("editTitulo").value,

          descricao: document.getElementById("editDescricao").value,

          prioridade: document.getElementById("editPrioridade").value,

          status: document.getElementById("editStatus").value,

          etiquetasIds,
        }),
      });

      if (!response.ok) {
        throw new Error("Erro ao atualizar");
      }

      fecharModalEdicao();

      carregarTarefas();
    } catch (error) {
      console.error(error);

      alert("Erro ao atualizar tarefa");
    }
  });

async function abrirDetalhes(tarefa) {
  tarefaAtual = tarefa;

  document.getElementById("detailsTitulo").innerText = tarefa.titulo;

  document.getElementById("detailsDescricao").innerText = tarefa.descricao;

  document.getElementById("taskDetailsModal").style.display = "flex";

  carregarEtiquetasEdicao(tarefa);
  carregarComentarios(tarefa.id);
}

function fecharDetalhes() {
  document.getElementById("taskDetailsModal").style.display = "none";
}

async function carregarComentarios(tarefaId) {
  try {
    const response = await fetch(`${API_URL}/comentarios/tarefa/${tarefaId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const comentarios = await response.json();

    const container = document.getElementById("commentsContainer");

    container.innerHTML = "";

    comentarios.forEach((comentario) => {
      const div = document.createElement("div");

      div.classList.add("comment");

      div.innerHTML = `
              <div class="comment-user">
                ${comentario.autor?.nome || "Usuário"}
              </div>

              <div class="comment-text">
                ${comentario.texto}
              </div>

              <div class="comment-date">
                ${new Date(comentario.criadoEm).toLocaleString("pt-BR")}
              </div>
            `;

      document.getElementById("commentsCount").innerText = comentarios.length;

      container.appendChild(div);
    });
  } catch (error) {
    console.error(error);
  }
}

document.getElementById("commentForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const texto = document.getElementById("novoComentario").value;

  try {
    const response = await fetch(`${API_URL}/comentarios`, {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },

      body: JSON.stringify({
        texto,
        tarefaId: tarefaAtual.id,
      }),
    });

    if (!response.ok) {
      throw new Error();
    }

    document.getElementById("novoComentario").value = "";

    carregarComentarios(tarefaAtual.id);
  } catch (error) {
    alert("Erro ao enviar comentário");
  }
});

const etiquetaModal = document.getElementById("etiquetaModal");

document
  .getElementById("openEtiquetaModalBtn")
  .addEventListener("click", () => {
    etiquetaModal.style.display = "flex";

    carregarEtiquetas();
  });

function fecharModalEtiqueta() {
  etiquetaModal.style.display = "none";
}

async function carregarEtiquetas() {
  try {
    const response = await fetch(`${API_URL}/etiquetas`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const etiquetas = await response.json();

    renderizarEtiquetas(etiquetas);
  } catch (error) {
    console.error(error);
  }
}

function renderizarEtiquetas(etiquetas) {
  const lista = document.getElementById("listaEtiquetas");

  const container = document.getElementById("etiquetasContainer");

  lista.innerHTML = "";
  container.innerHTML = "";

  etiquetas.forEach((etiqueta) => {
    lista.innerHTML += `
      <div
        class="etiqueta-chip"
        style="
          background:${etiqueta.cor}20;
          color:${etiqueta.cor};
          border:1px solid ${etiqueta.cor}40;
        "
      >
        ${etiqueta.nome}
      </div>
    `;

    container.innerHTML += `
      <label
        class="checkbox-tag"
        style="
          background:${etiqueta.cor}20;
          color:${etiqueta.cor};
          border:1px solid ${etiqueta.cor}40;
        "
      >

        <input
          type="checkbox"
          value="${etiqueta.id}"
        >

        ${etiqueta.nome}

      </label>
    `;
  });
}

document
  .getElementById("etiquetaForm")
  .addEventListener("submit", async (e) => {
    e.preventDefault();

    const nome = document.getElementById("etiquetaNome").value;

    const cor = document.getElementById("etiquetaCor").value;

    try {
      const response = await fetch(`${API_URL}/etiquetas`, {
        method: "POST",

        headers: {
          "Content-Type": "application/json",

          Authorization: `Bearer ${token}`,
        },

        body: JSON.stringify({
          nome,
          cor,
        }),
      });

      if (!response.ok) {
        throw new Error();
      }

      document.getElementById("etiquetaForm").reset();

      carregarEtiquetas();
    } catch (error) {
      alert("Erro ao criar etiqueta");
    }
  });

function fecharUnlockModal() {
  document.getElementById("unlockModal").style.display = "none";

  document.getElementById("unlockPassword").value = "";
}

async function desbloquearTarefa() {
  const senha = document.getElementById("unlockPassword").value;

  try {
    const response = await fetch(
      `${API_URL}/tarefas/${tarefaBloqueadaId}/desbloquear`,
      {
        method: "POST",

        headers: {
          "Content-Type": "application/json",

          Authorization: `Bearer ${token}`,
        },

        body: JSON.stringify({
          senha,
        }),
      },
    );

    if (!response.ok) {
      alert("Senha incorreta");

      return;
    }

    const tarefa = await response.json();

    fecharUnlockModal();

    abrirDetalhes(tarefa);
  } catch (error) {
    console.error(error);

    alert("Erro ao desbloquear");
  }
}

function confirmarExclusao(id) {
  tarefaExcluirId = id;

  document.getElementById("deleteModal").style.display = "flex";
}

function fecharDeleteModal() {
  document.getElementById("deleteModal").style.display = "none";

  tarefaExcluirId = null;
}

async function excluirTarefa() {
  try {
    const response = await fetch(`${API_URL}/tarefas/${tarefaExcluirId}`, {
      method: "DELETE",

      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error("Erro ao excluir");
    }

    fecharDeleteModal();

    carregarTarefas();
  } catch (error) {
    alert(error.message);
  }
}

async function carregarEtiquetasEdicao(tarefa) {
  const response = await fetch(`${API_URL}/etiquetas`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  const etiquetas = await response.json();

  const container = document.getElementById("editEtiquetasContainer");

  container.innerHTML = "";

  etiquetas.forEach((etiqueta) => {
    const selecionada = tarefa.etiquetas?.some((e) => e.id === etiqueta.id);

    container.innerHTML += `
      <label
        class="checkbox-tag"
        style="
          background:${etiqueta.cor}20;
          color:${etiqueta.cor};
          border:1px solid ${etiqueta.cor}40;
        "
      >

        <input
          type="checkbox"
          value="${etiqueta.id}"
          ${selecionada ? "checked" : ""}
        >

        ${etiqueta.nome}

      </label>
    `;
  });
}

configurarDragAndDrop();

carregarEtiquetas();
carregarProjeto();
carregarTarefas();
