/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package pl.com.bottega.ecommerce.sales.domain.offer;

import java.math.BigDecimal;
import java.util.Date;

public class OfferItem {

    private Money totalValue;
    private Discount discount = null;
    private int quantity;
    private Product product = null;

    public OfferItem(Product product, int quantity, Money totalValue) {
        this( product, quantity, totalValue, null );
    }

    public OfferItem(Product product, int quantity, Money totalValue, Discount discount) {
        this.product = product;
        this.totalValue = totalValue;
        this.quantity = quantity;
        this.discount = discount;

        BigDecimal discountValue = new BigDecimal( 0 );
        if (discount != null) {
            discountValue = discountValue.subtract( discount.getMoney().getValue() );
        }

        this.product.cost.value = product.price.multiply( new BigDecimal( quantity ) ).subtract( discountValue );
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (discount.getMoney() == null ? 0 : discount.getMoney().hashCode());
        result = prime * result + (product.name == null ? 0 : product.name.hashCode());
        result = prime * result + (product.price == null ? 0 : product.price.hashCode());
        result = prime * result + (product.id == null ? 0 : product.id.hashCode());
        result = prime * result + (product.type == null ? 0 : product.type.hashCode());
        result = prime * result + quantity;
        result = prime * result + (product.cost == null ? 0 : product.cost.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OfferItem other = (OfferItem) obj;
        if (discount == null) {
            if (other.discount != null) {
                return false;
            }
        } else if (!discount.equals( other.discount )) {
            return false;
        }
        if (product == null) {
            if (other.product != null) {
                return false;
            }
        } else if (!product.equals( other.product )) {
            return false;
        }
        return true;
    }

    /**
     * @param other
     * @param delta acceptable percentage difference
     * @return
     */
    public boolean sameAs(OfferItem other, double delta) {
        if (product == null) {
            if (other.product != null) {
                return false;
            }
        } else if (!product.equals( other.product )) {
            return false;
        }
        if (quantity != other.quantity) {
            return false;
        }

        BigDecimal max;
        BigDecimal min;
        if (product.cost.getValue().compareTo( other.product.cost.getValue() ) > 0) {
            max = product.cost.getValue();
            min = other.product.cost.getValue();
        } else {
            max = other.product.cost.getValue();
            min = product.cost.getValue();
        }

        BigDecimal difference = max.subtract( min );
        BigDecimal acceptableDelta = max.multiply( BigDecimal.valueOf( delta / 100 ) );

        return acceptableDelta.compareTo( difference ) > 0;
    }

}
