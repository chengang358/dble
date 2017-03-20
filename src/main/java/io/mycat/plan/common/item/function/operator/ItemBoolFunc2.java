package io.mycat.plan.common.item.function.operator;

import io.mycat.plan.common.MySQLcom;
import io.mycat.plan.common.item.Item;
import io.mycat.plan.common.item.function.operator.cmpfunc.util.ArgComparator;
import io.mycat.plan.common.item.function.primary.ItemBoolFunc;

/**
 * Bool with 2 string args
 * 
 * 
 */
public abstract class ItemBoolFunc2 extends ItemBoolFunc {
	protected ArgComparator cmp;
	protected boolean abort_on_null;

	public ItemBoolFunc2(Item a, Item b) {
		super(a, b);
		cmp = new ArgComparator(a, b);
		abort_on_null = false;
	}

	public int set_cmp_func() {
		return cmp.setCmpFunc(this, args.get(0), args.get(1), true);
	}

	@Override
	public void fixLengthAndDec() {
		maxLength = 1; // Function returns 0 or 1

		/*
		 * As some compare functions are generated after sql_yacc, we have to
		 * check for out of memory conditions here
		 */
		if (args.get(0) == null || args.get(1) == null)
			return;

		/*
		 * See agg_item_charsets() in item.cc for comments on character set and
		 * collation aggregation.
		 */

		args.get(0).cmpContext = args.get(1).cmpContext = MySQLcom.item_cmp_type(args.get(0).resultType(),
				args.get(1).resultType());
		// Make a special case of compare with fields to get nicer DATE
		// comparisons

		set_cmp_func();
		return;
	}
	
	@Override
	public boolean isNull() {
		return args.get(0).isNull() || args.get(1).isNull();
	}
}