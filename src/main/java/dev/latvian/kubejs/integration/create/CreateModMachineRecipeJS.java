package dev.latvian.kubejs.integration.create;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CreateModMachineRecipeJS extends RecipeJS
{
	public IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
	public List<ItemStackJS> results = new ArrayList<>();

	@Override
	public void create(ListJS args)
	{
		if (args.size() < 2)
		{
			throw new RecipeExceptionJS("Create machine recipe requires at least 2 arguments - result array and ingredient!");
		}

		ListJS results1 = ListJS.orSelf(args.get(0));

		if (results1.isEmpty())
		{
			throw new RecipeExceptionJS("Create machine recipe results can't be empty!");
		}

		for (Object o : results1)
		{
			ItemStackJS stack = ItemStackJS.of(o);

			if (stack.isEmpty())
			{
				throw new RecipeExceptionJS("Create machine recipe result " + o + " is not a valid item!");
			}
			else
			{
				results.add(stack);
			}
		}

		ingredient = IngredientJS.of(args.get(1));

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Create machine recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}

		if (args.size() >= 3)
		{
			time(((Number) args.get(2)).intValue());
		}
	}

	@Override
	public void deserialize()
	{
		for (JsonElement e : json.get("results").getAsJsonArray())
		{
			ItemStackJS stack = ItemStackJS.resultFromRecipeJson(e);

			if (stack.isEmpty())
			{
				throw new RecipeExceptionJS("Create machine recipe result " + e + " is not a valid item!");
			}
			else
			{
				results.add(stack);
			}
		}

		if (results.isEmpty())
		{
			throw new RecipeExceptionJS("Create machine recipe results can't be empty!");
		}

		JsonElement in = json.get("ingredients").getAsJsonArray().get(0);
		ingredient = IngredientJS.ingredientFromRecipeJson(in);

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Create machine recipe ingredient " + in + " is not a valid ingredient!");
		}
	}

	@Override
	public void serialize()
	{
		JsonArray ingredientsJson = new JsonArray();
		ingredientsJson.add(ingredient.toJson());
		json.add("ingredients", ingredientsJson);

		JsonArray resultsJson = new JsonArray();

		for (ItemStackJS stack : results)
		{
			resultsJson.add(stack.getResultJson());
		}

		json.add("results", resultsJson);

		if (!json.has("processingTime"))
		{
			json.addProperty("processingTime", 300);
		}
	}

	public CreateModMachineRecipeJS time(int t)
	{
		json.addProperty("processingTime", Math.max(t, 0));
		return this;
	}

	@Override
	public Collection<IngredientJS> getInput()
	{
		return Collections.singleton(ingredient);
	}

	@Override
	public boolean replaceInput(Object i, Object with)
	{
		if (ingredient.anyStackMatches(IngredientJS.of(i)))
		{
			ingredient = IngredientJS.of(with);
			return true;
		}

		return false;
	}

	@Override
	public Collection<ItemStackJS> getOutput()
	{
		return results;
	}

	@Override
	public boolean replaceOutput(Object i, Object with)
	{
		boolean changed = false;

		for (int j = 0; j < results.size(); j++)
		{
			if (IngredientJS.of(i).test(results.get(j)))
			{
				results.set(j, ItemStackJS.of(with));
				changed = true;
			}
		}

		return changed;
	}
}