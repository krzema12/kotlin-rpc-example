import it.krzeminski.todoapp.api.Todo
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.INPUT
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.h1
import react.dom.input
import react.dom.li
import react.dom.render
import react.dom.ul
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch
import react.setState

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            browserRouter {
                switch {
                    route("/", exact = true) {
                        child(TodoApp::class) {}
                    }
                }
            }
        }
    }
}

external interface TodoAppState : RState {
    var todos: List<Todo>
    var newTodoDescription: String
}

class TodoApp : RComponent<RProps, TodoAppState>(), CoroutineScope by MainScope() {
    override fun TodoAppState.init() {
        todos = emptyList()
    }

    override fun componentDidMount() {
        fetchTodos()
    }

    override fun RBuilder.render() {
        h1 { +"TODOs:" }
        if (state.todos.isNotEmpty()) {
            ul {
                state.todos.forEach { todo ->
                    li {
                        +"${todo.description} (assignee: ${todo.assignee ?: "unassigned"}) - done: ${todo.isDone}"
                    }
                }
            }
        } else {
            div { +"No TODOs!" }
        }
        input {
          attrs {
              value = state.newTodoDescription
              onChangeFunction = { event ->
                  val newValue = (event.target as HTMLInputElement).value
                  setState {
                      newTodoDescription = newValue
                  }
              }
          }
        }
        input(type = InputType.button) {
            attrs {
                value = "Add"
                onClickFunction = {
                    addNewTodoAndUpdateList()
                }
            }
        }
    }

    private fun fetchTodos() {
        with(TodoAppApiJsClient(url = "http://localhost:8080", coroutineContext)) {
            launch {
                val fetchedTodos = listTodos()
                setState {
                    todos = fetchedTodos
                }
            }
        }
    }

    private fun addNewTodoAndUpdateList() {
        with(TodoAppApiJsClient(url = "http://localhost:8080", coroutineContext)) {
            launch {
                addToList(state.newTodoDescription)
                val fetchedTodos = listTodos()
                setState {
                    todos = fetchedTodos
                }
            }
        }
    }
}
