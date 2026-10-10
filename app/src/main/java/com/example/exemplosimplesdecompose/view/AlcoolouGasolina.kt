package com.example.exemplosimplesdecompose.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import com.example.exemplosimplesdecompose.R

@Composable
fun AlcoolGasolinaPreco(navController: NavHostController) {
    var alcool by remember { mutableStateOf("") }
    var gasolina by remember { mutableStateOf("") }
    
    val context = LocalContext.current

    val sharedPreferences = remember {
        context.getSharedPreferences("ConfiguracoesApp", Context.MODE_PRIVATE)
    }
    var checkedState by remember { 
        mutableStateOf(sharedPreferences.getBoolean("switch_estado", true)) 
    }
    
    var resultado by remember { mutableStateOf("") }

    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currSymbol = stringResource(R.string.currency_symbol)
            
            OutlinedTextField(
                value = alcool,
                onValueChange = { alcool = it }, // Atualiza o estado
                label = { Text(stringResource(R.string.alcohol_price_label, currSymbol)) },
                modifier = Modifier.fillMaxWidth(), // Preenche a largura disponível
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Configuração do teclado
            )
            OutlinedTextField(
                value = gasolina,
                onValueChange = { gasolina = it },
                label = { Text(stringResource(R.string.gasoline_price_label, currSymbol)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )


            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.Start) {
                Text(
                    text = if (checkedState) stringResource(R.string.limit_75) else stringResource(R.string.limit_70),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp, end = 16.dp)
                )
                Switch(
                    modifier = Modifier.semantics { contentDescription = "Demo with icon" },
                    checked = checkedState,
                    onCheckedChange = { novoEstado -> 
                        checkedState = novoEstado 
                        sharedPreferences.edit().putBoolean("switch_estado", novoEstado).apply()
                    },
                    thumbContent = {
                        if (checkedState) {
                            // Icon isn't focusable, no need for content description
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }
                )
            }
            val alcBetter = stringResource(R.string.alcohol_better)
            val gasBetter = stringResource(R.string.gasoline_better)
            val invalidVal = stringResource(R.string.invalid_values)

            // Botão de cálculo
            Button(
                onClick = {
                    val pAlcool = alcool.replace(",", ".").toDoubleOrNull()
                    val pGasolina = gasolina.replace(",", ".").toDoubleOrNull()
                    
                    if (pAlcool != null && pGasolina != null && pGasolina > 0) {
                        val proporcao = pAlcool / pGasolina
                        val limite = if (checkedState) 0.75 else 0.70
                        val formatador = java.text.DecimalFormat("#.##")
                        val percentual = formatador.format(proporcao * 100) + "%"
                        
                        resultado = if (proporcao <= limite) {
                            "$alcBetter ($percentual)"
                        } else {
                            "$gasBetter ($percentual)"
                        }
                    } else {
                        resultado = invalidVal
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.calculate))
            }

            // Texto do resultado
            Text(
                text = resultado,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = { navController.navigate("gas_list") },
                ) {
                    Text(stringResource(R.string.view_gas_stations))
                }
                
                FloatingActionButton(
                    onClick = { 
                        navController.navigate("cadastro_posto?alcool=${alcool}&gasolina=${gasolina}")
                    },
                ) {
                    Icon(Icons.Filled.Add, stringResource(R.string.add_station))
                }
            }
        }
    }
}
