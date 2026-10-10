package com.example.exemplosimplesdecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exemplosimplesdecompose.ui.theme.ExemploSimplesDeComposeTheme
import com.example.exemplosimplesdecompose.view.AlcoolGasolinaPreco
import com.example.exemplosimplesdecompose.view.GasList
import com.example.exemplosimplesdecompose.view.Welcome

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExemploSimplesDeComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController: NavHostController = rememberNavController()
                    NavHost(navController = navController, startDestination = "welcome") {
                        composable("welcome") { Welcome(navController) }
                        composable("gas_list") { 
                            GasList(navController)
                        }
                        composable("main_alc_gas") { AlcoolGasolinaPreco(navController) }
                        composable(
                            route = "cadastro_posto?alcool={alcool}&gasolina={gasolina}&postoId={postoId}",
                            arguments = listOf(
                                androidx.navigation.navArgument("alcool") { nullable = true },
                                androidx.navigation.navArgument("gasolina") { nullable = true },
                                androidx.navigation.navArgument("postoId") { nullable = true }
                            )
                        ) { backStackEntry ->
                            val alcool = backStackEntry.arguments?.getString("alcool")
                            val gasolina = backStackEntry.arguments?.getString("gasolina")
                            val postoId = backStackEntry.arguments?.getString("postoId")
                            com.example.exemplosimplesdecompose.view.CadastroPosto(navController, alcool, gasolina, postoId)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExemploSimplesDeComposeTheme {
        Greeting("Android")
    }
}