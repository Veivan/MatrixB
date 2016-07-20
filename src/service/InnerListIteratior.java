package service;

import inrtfs.IAggregate;

import java.util.Iterator;

public class InnerListIteratior implements Iterator<Object> {

    private IAggregate aggr;
    private int index = -1; // Points to inner aggr position

    // Constructor
    public InnerListIteratior(IAggregate aggr)
    {
        this.aggr = aggr;
    }

	public Object First() {
		if (aggr.Count() > 0)
		{
			index = 0;
    		return aggr.Element(index);			
		}
		return null;
	}

   @Override
	public boolean hasNext() {
		return (index > -1 && index < aggr.Count());
	}

	@Override
	public Object next() {
		index++;		
        if (index < aggr.Count())
        {
    		return aggr.Element(index);			
        }
		return null;
	}

}
