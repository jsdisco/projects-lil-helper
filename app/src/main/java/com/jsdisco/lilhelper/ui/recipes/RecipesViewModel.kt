package com.jsdisco.lilhelper.ui.recipes

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.jsdisco.lilhelper.data.AppRepository
import com.jsdisco.lilhelper.data.local.models.Note
import com.jsdisco.lilhelper.data.local.models.relations.RecipeWithIngredients
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }

class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AppRepository.getRepoInstance(application)

    val recipes = repo.recipes
    val currRecipe = repo.currRecipe
    val loadImgs = repo.settingsLoadImgs
    private val settingsIngs = repo.settingsIngs

    private val _loading = MutableLiveData<ApiStatus>()
    val loading: LiveData<ApiStatus> = _loading

    fun downloadRecipesFromApi(){
        viewModelScope.launch {
            _loading.value = ApiStatus.LOADING
            try {
                repo.getRecipesFromApi()
                _loading.value = ApiStatus.DONE
            } catch(e: Exception){
                Log.e("RecipesViewModel", "Error downloading recipes from API: $e")
                _loading.value = ApiStatus.ERROR
            }
        }
    }

    fun filterRecipes(by: String) : List<RecipeWithIngredients>? {
        val filtered = recipes.value?.filter{recipe ->
            when(by){
                "warm" -> recipe.recipe.r_cat_warm
                "cold" -> recipe.recipe.r_cat_cold
                "salad" -> recipe.recipe.r_cat_salad
                "soup" -> recipe.recipe.r_cat_soup
                "sweet" -> recipe.recipe.r_cat_sweet
                "base" -> recipe.recipe.r_cat_base
                else -> true
            }
        }
        return filtered
    }

    fun getRecipeWithIngredientsById(id: String){
        viewModelScope.launch {
            repo.getRecipeWithIngredientsById(id)
        }
    }

    fun resetLoadingStatus(){
        _loading.value = ApiStatus.DONE
    }

    fun createNoteFromRecipe(){
        if (currRecipe.value != null){
            val recipe = currRecipe.value!!
            val excludedIngs =
                settingsIngs.value?.filter { !it.si_included }?.map { it.si_name }

            val ingItems = if (excludedIngs != null) {
                val filtered =
                    recipe.ingredients.filter { !excludedIngs.contains(it.i_name) }
                filtered.joinToString("\n") { "${it.i_name} ${it.i_amount} ${it.i_unit} " }
            } else {
                recipe.ingredients.joinToString("\n") { "${it.i_name} ${it.i_amount} ${it.i_unit} " }
            }

            val noteFromRecipe = Note(title = recipe.recipe.r_title, content = ingItems)
            viewModelScope.launch {
                repo.insertNote(noteFromRecipe)
            }
        }

    }
}