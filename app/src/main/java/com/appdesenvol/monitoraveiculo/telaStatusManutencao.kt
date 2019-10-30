package com.appdesenvol.monitoraveiculo


import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.appdesenvol.monitoraveiculo.bancodados.BancoDadoConfig
import com.appdesenvol.monitoraveiculo.logica.AdapterRecyclerView
import com.appdesenvol.monitoraveiculo.logica.ControleManutencoes
import com.appdesenvol.monitoraveiculo.logica.ControleDados
import com.appdesenvol.monitoraveiculo.objetos.Veiculo
import com.appdesenvol.monitoraveiculo.objetos.VeiculoManutencao
import kotlinx.android.synthetic.main.activity_tela_principalmain.*
import kotlinx.android.synthetic.main.content_tela_status_manutencao.*
import kotlinx.android.synthetic.main.tipo_manutencao.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class telaStatusManutencao : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private val controleManutencoes = ControleManutencoes()
    private lateinit var bd: BancoDadoConfig
    private var veiculoId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_status_manutencao)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bd = BancoDadoConfig.getInstance(applicationContext)

        val buscarVeiculo = buscaVeiculo
        val listId = mutableListOf<Long>(0)
        val listVeiculo = mutableListOf<String>("\t\t\t\t\t\t Buscar Veiculo")


        buscarVeiculo.adapter = ArrayAdapter<Long>(this, android.R.layout.preference_category, listId)
        buscarVeiculo.adapter = ArrayAdapter<String>(this, android.R.layout.preference_category, listVeiculo)

        buscarVeiculo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                veiculoId = listId[position]

                ControleDados.pegarVeiculoId(veiculoId)

                veiculoManutencao(veiculoId)


                Log.i("MEU ID", " ID " + veiculoId)

            }

        }

        CoroutineScope(IO).launch {

            try {

                val buscveiculo = bd.controleDAO().buscaVeiculo()

                //val buscmanutencao = bd.controleDAO().buscaManutencao()

                for (v: Veiculo in buscveiculo) {

                    listVeiculo.add(" \t\t\t\t\t\t " + v.nomeVeiculo)
                    listId.add(v.idV)
                }

            } catch (e: Exception) {

                Log.e("ERRO", " VERIFICAR ERRO: " + e)

            }
        }


        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewManutencao.layoutManager = layoutManager
        recyclerViewManutencao.adapter = AdapterRecyclerView(controleManutencoes.arrayManutencoes,this)

        recyclerViewManutencao.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun veiculoManutencao(veiculoId: Long) {

            CoroutineScope(IO).launch {

                try {

                    val buscveiculomanutencao = bd.controleDAO().buscaVeiculoManutencao(veiculoId)

                    for (vm: VeiculoManutencao in buscveiculomanutencao) {

                        controleManutencoes.resultManutencao(
                            vm.manutencao.tipoManutencao,
                            vm.manutencao.kmtroca,
                            vm.manutencao.data,
                            vm.manutencao.custo
                        )

                    }


                } catch (e: Exception) {

                    Log.e("ERRO2", " VERIFICAR ERRO2: " + e)

                }

            }
            recyclerViewManutencao.adapter?.notifyDataSetChanged()

    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.tela_status_manutencao, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        ControleDados.excluirVeiculo(veiculoId, this)

        return when (item.itemId) {
            R.id.btExcluir -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.Home -> {
                //Chamar a tela de cadastro
                startActivity(Intent(this@telaStatusManutencao, telaPrincipalmain::class.java))
            }
            R.id.CadastroVeiculo -> {
                //Chamar a tela de cadastro
                startActivity(Intent(this@telaStatusManutencao, telaCadastro::class.java))
            }
            R.id.StatusManuteção -> {

            }
            R.id.RelatorioCusto -> {

            }
            R.id.SobreApp -> {
                startActivity(Intent(this@telaStatusManutencao, telaSobreApp::class.java))


            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
