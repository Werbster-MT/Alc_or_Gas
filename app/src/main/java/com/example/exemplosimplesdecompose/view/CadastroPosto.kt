package com.example.exemplosimplesdecompose.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.data.Coordenadas
import com.example.exemplosimplesdecompose.data.Historico
import com.example.exemplosimplesdecompose.data.Posto
import com.example.exemplosimplesdecompose.data.PostoRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("MissingPermission")
private fun pegarLocalizacao(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationObtained: (String, String) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
        if (location != null) {
            onLocationObtained(location.latitude.toString(), location.longitude.toString())
        } else {
            Toast.makeText(context, context.getString(R.string.location_unavailable), Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroPosto(
    navController: NavHostController,
    alcoolParam: String? = null,
    gasolinaParam: String? = null,
    postoId: String? = null
) {
    val context = LocalContext.current
    val postoRepository = remember { PostoRepository(context) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var nomeDoPosto by remember { mutableStateOf("") }
    var alcool by remember { mutableStateOf(alcoolParam ?: "") }
    var gasolina by remember { mutableStateOf(gasolinaParam ?: "") }
    var latitude by remember { mutableStateOf("-23.5505") }
    var longitude by remember { mutableStateOf("-46.6333") }
    var dataAtualizacao by remember { mutableStateOf("") }
    var historico by remember { mutableStateOf<List<Historico>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val sharedPreferences = remember {
        context.getSharedPreferences("ConfiguracoesApp", Context.MODE_PRIVATE)
    }
    var checkedState by remember { 
        mutableStateOf(sharedPreferences.getBoolean("switch_estado", true)) 
    }
    var resultado by remember { mutableStateOf("") }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pegarLocalizacao(context, fusedLocationClient) { lat, lng ->
                latitude = lat
                longitude = lng
            }
        } else {
            Toast.makeText(context, context.getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(postoId) {
        if (postoId != null) {
            val postoParaEditar = postoRepository.getPostos().find { it.id == postoId }
            if (postoParaEditar != null) {
                nomeDoPosto = postoParaEditar.nome
                alcool = if (postoParaEditar.precoAlcool > 0) postoParaEditar.precoAlcool.toString() else ""
                gasolina = if (postoParaEditar.precoGasolina > 0) postoParaEditar.precoGasolina.toString() else ""
                latitude = postoParaEditar.coordenadas.latitude.toString()
                longitude = postoParaEditar.coordenadas.longitude.toString()
                dataAtualizacao = postoParaEditar.dataAtualizacao
                historico = postoParaEditar.historico
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (postoId != null) stringResource(R.string.station_details) else stringResource(R.string.register_station)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    if (postoId != null) {
                        IconButton(onClick = {
                            showDeleteDialog = true
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_station))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.delete_station_title)) },
                text = { Text(stringResource(R.string.delete_station_confirm)) },
                confirmButton = {
                    TextButton(onClick = {
                        if (postoId != null) {
                            postoRepository.excluirPosto(postoId)
                            Toast.makeText(context, context.getString(R.string.station_deleted), Toast.LENGTH_SHORT).show()
                        }
                        showDeleteDialog = false
                        navController.popBackStack()
                    }) {
                        Text(stringResource(R.string.confirm_yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.confirm_no))
                    }
                }
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.station_data), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(
                            value = nomeDoPosto,
                            onValueChange = { nomeDoPosto = it },
                            label = { Text(stringResource(R.string.station_name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.prices_and_analysis), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        
                        OutlinedTextField(
                            value = alcool,
                            onValueChange = { alcool = it },
                            label = { Text(stringResource(R.string.alcohol_price_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        
                        OutlinedTextField(
                            value = gasolina,
                            onValueChange = { gasolina = it },
                            label = { Text(stringResource(R.string.gasoline_price_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (checkedState) stringResource(R.string.limit_75) else stringResource(R.string.limit_70),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Switch(
                                checked = checkedState,
                                onCheckedChange = { novoEstado -> 
                                    checkedState = novoEstado 
                                    sharedPreferences.edit().putBoolean("switch_estado", novoEstado).apply()
                                }
                            )
                        }
                        
                        val alcBetter = stringResource(R.string.alcohol_better)
                        val gasBetter = stringResource(R.string.gasoline_better)
                        val invalidVal = stringResource(R.string.invalid_values)

                        Button(
                            onClick = {
                                val pAlcool = alcool.replace(",", ".").toDoubleOrNull()
                                val pGasolina = gasolina.replace(",", ".").toDoubleOrNull()
                                if (pAlcool != null && pGasolina != null && pGasolina > 0) {
                                    val proporcao = pAlcool / pGasolina
                                    val limite = if (checkedState) 0.75 else 0.70
                                    resultado = if (proporcao <= limite) alcBetter else gasBetter
                                } else {
                                    resultado = invalidVal
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                        ) {
                            Text(stringResource(R.string.calculate_advantage))
                        }
                        
                        if (resultado.isNotEmpty()) {
                            Text(
                                text = resultado, 
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(stringResource(R.string.location), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = latitude,
                                onValueChange = { latitude = it },
                                label = { Text(stringResource(R.string.latitude)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = longitude,
                                onValueChange = { longitude = it },
                                label = { Text(stringResource(R.string.longitude)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        
                        Button(
                            onClick = {
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        pegarLocalizacao(context, fusedLocationClient) { lat, lng ->
                                            latitude = lat
                                            longitude = lng
                                        }
                                    }
                                    else -> {
                                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.use_current_location))
                        }
                        
                        if (postoId != null) {
                            val noMapsMsg = stringResource(R.string.no_maps)
                            Button(
                                onClick = {
                                    val encodedName = Uri.encode(nomeDoPosto)
                                    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($encodedName)")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(context, noMapsMsg, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(stringResource(R.string.view_location_map))
                            }
                        }
                    }
                }
            }

            if (historico.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.history_last_3), 
                        style = MaterialTheme.typography.titleMedium, 
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
                item {
                    Column {
                        val alcInit = stringResource(R.string.alcohol_initial)
                        val gasInit = stringResource(R.string.gasoline_initial)
                        val currSymbol = stringResource(R.string.currency_symbol)
                        
                        historico.forEach { h ->
                            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = h.data, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "$alcInit: $currSymbol ${h.precoAlcool} | $gasInit: $currSymbol ${h.precoGasolina}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = stringResource(R.string.recommended, h.melhorOpcao, h.parametro), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }
            } else if (postoId != null) {
                item {
                    Text(
                        text = stringResource(R.string.no_history),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                val alcBetter = stringResource(R.string.alcohol)
                val gasBetter = stringResource(R.string.gasoline)
                val savedMsg = stringResource(R.string.station_saved)
                val noNameMsg = stringResource(R.string.enter_station_name)
                
                Button(
                    onClick = {
                        if (nomeDoPosto.isNotBlank()) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            val dataAtual = sdf.format(Date())
                            val pAlcool = alcool.replace(",", ".").toDoubleOrNull() ?: 0.0
                            val pGasolina = gasolina.replace(",", ".").toDoubleOrNull() ?: 0.0
                            val lat = latitude.replace(",", ".").toDoubleOrNull() ?: -23.5505
                            val lng = longitude.replace(",", ".").toDoubleOrNull() ?: -46.6333
                            
                            val proporcao = pAlcool / (if (pGasolina > 0) pGasolina else 1.0)
                            val parametroUsado = if (checkedState) "75%" else "70%"
                            val limite = if (checkedState) 0.75 else 0.70
                            val melhorOpcao = if (proporcao <= limite) alcBetter else gasBetter
                            
                            val novoHistorico = Historico(dataAtual, pAlcool, pGasolina, melhorOpcao, parametroUsado)
                            
                            val historicoAtual = historico
                            val historicoAtualizado = (historicoAtual + novoHistorico).takeLast(3)

                            val posto = Posto(
                                id = postoId ?: java.util.UUID.randomUUID().toString(),
                                nome = nomeDoPosto,
                                precoAlcool = pAlcool,
                                precoGasolina = pGasolina,
                                dataAtualizacao = dataAtual,
                                coordenadas = Coordenadas(lat, lng),
                                historico = historicoAtualizado
                            )
                            postoRepository.salvarPosto(posto)
                            Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                            navController.popBackStack() // Volta para a tela anterior
                        } else {
                            Toast.makeText(context, noNameMsg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(stringResource(R.string.save_station))
                }
            }
        }
    }
}


