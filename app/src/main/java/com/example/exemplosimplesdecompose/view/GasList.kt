package com.example.exemplosimplesdecompose.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.data.PostoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasList(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { PostoRepository(context) }
    var postos by remember { mutableStateOf(repository.getPostos()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.gas_list_title)) }
            )
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = { navController.navigate("cadastro_posto") }
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_station))
            }
        }
    ) { innerPadding ->
        if (postos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_station_registered),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(postos) { posto ->
                    Card(
                        onClick = {
                            // Navega para a tela de detalhes / edição
                            navController.navigate("cadastro_posto?postoId=${posto.id}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.gas_station_icon),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = posto.nome,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.padding(2.dp))
                                Text(
                                    text = "${stringResource(R.string.alcohol)}: R$ ${posto.precoAlcool}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${stringResource(R.string.gasoline)}: R$ ${posto.precoGasolina}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}