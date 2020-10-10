import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.setState
import styled.css
import styled.styledDiv
import styled.styledInput

external interface WelcomeProps : RProps {
    var initName: String
}

external interface WelcomeState : RState {
    var name: String
    var zoosFromBackend: String?
}

@JsExport
class Welcome(props: WelcomeProps) : RComponent<WelcomeProps, WelcomeState>(props), CoroutineScope by MainScope() {

    override fun WelcomeState.init(props: WelcomeProps) {
        name = props.initName
    }

    override fun componentDidMount() {
        with (ZooClient(coroutineContext)) {
            launch {
                val zoosFromBackendFetched = getZoos()
                setState {
                    zoosFromBackend = zoosFromBackendFetched
                }
            }
        }
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                +WelcomeStyles.textContainer
            }
            +"Hello, ${state.name}"
            +"From backend: ${state.zoosFromBackend}"
        }
        styledInput {
            css {
                +WelcomeStyles.textInput
            }
            attrs {
                type = InputType.text
                value = state.name
                onChangeFunction = { event ->
                    setState {
                        name = (event.target as HTMLInputElement).value
                    }
                }
            }
        }
    }
}