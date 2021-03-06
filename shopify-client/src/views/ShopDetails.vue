<template>
  <div>
    <p v-if="error">{{ error }}</p>
    <div v-if="shop">
      <div id="shop-details-wrapper">
        <b-container id="shop-details">
          <b-row class="text-left">
            <b-col sm="6">
              <b-col sm="12">
                <h1>{{ shop.name }}</h1>
              </b-col>
              <b-col sm="12">
                <h4>{{ shop.description }}</h4>
              </b-col>
              <b-card-text class="shopAttributeBlock">
                <div
                  v-for="category in shop.categories"
                  v-bind:key="'shop' + shop.id + '_category' + category.id"
                  v-bind:class="'shopAttribute shopCategory ' + category.name"
                >
                  {{category.name}}
                </div>
                <div
                  v-for="tag in shop.tags"
                  v-bind:key="'shop' + shop.id + '_tag' + tag.id"
                  v-bind:class="'shopAttribute shopTag ' + tag.name"
                >
                  {{tag.name}}
                </div>
              </b-card-text>
              <b-col sm="12">
                <b-button v-on:click="updateShop()" variant="primary">
                  Update Shop
                </b-button>
              </b-col>
              <b-col sm="12">
                <b-button
                  v-on:click="scrollToProductCatalog({ behavior: 'smooth' })"
                  variant="primary"
                >
                  Browse Products
                </b-button>
              </b-col>
              <b-col sm="12">
                <b-button v-on:click="createProduct()" variant="primary">
                  Add Product
                </b-button>
              </b-col>
              <b-col sm="12">
                <b-button v-on:click="deleteShop()" variant="danger">
                  Delete Shop
                </b-button>
              </b-col>
            </b-col>
            <b-col sm="6">
              <b-col sm="12">
                <b-img id="shop-image" v-bind:src="shop.image"></b-img>
              </b-col>
            </b-col>
          </b-row>
        </b-container>
      </div>
      <div id="productCatalogWrapper">
        <b-container id="productCatalog" ref="productCatalog">
          <h2>Product Catalog</h2>
          <div v-if="shop.products.length">
            <b-form-group>
              <b-form-input v-model="keyword" placeholder="Search product"></b-form-input>
            </b-form-group>
            <b-row cols="4">
              <b-col v-for="product in shop.products.filter(p =>
                        !keyword|| p.name.toLowerCase().includes(keyword.toLowerCase()))" :key="product.name">
                <b-card
                  v-bind:title="product.name"
                  v-bind:img-src="product.image"
                  v-bind:img-alt="product.name"
                  img-top
                  img-height="60%"
                  tag="article"
                  style="max-width: 20rem; height: 100%;"
                  class="mb-2"
                >
                  <b-card-text>
                    {{ product.description }}
                  </b-card-text>

                  <b-button
                    v-bind:href="'/app/shops/' + id + '/products/' + product.id"
                    variant="info"
                    class="card-button"
                    >Details</b-button
                  >
                  <b-button href="#" variant="primary" class="card-button" @click="addProductToCart(product)"
                    >Add to Cart</b-button
                  >
                </b-card>
              </b-col>
            </b-row>
          </div>
          <p v-else>No products in shop</p>
        </b-container>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ShopDetails",
  data: function() {
    return {
      shop: null,
      error: "",
      keyword: "",
    };
  },
  methods: {
    scrollToProductCatalog(options) {
      const el = this.$refs.productCatalog;

      if (el) {
        el.scrollIntoView(options);
      }
    },
    updateShop() {
      // Redirect to Update Store Page
      this.$router.push(`/app/shops/${this.id}/update`);
    },
    createProduct() {
      // Redirect to Create Page Page
      this.$router.push(`/app/shops/${this.id}/products/create`);
    },
    addProductToCart(product) {
      this.$store.commit('addProduct', product);
    },
    deleteShop() {

      this.$bvModal.msgBoxConfirm(`Are you sure you wish to delete shop: ${this.shop.name}`, {
                title: 'Delete Shop Confirmation',
                size: 'sm',
                buttonSize: 'sm',
                okVariant: 'danger',
                okTitle: 'YES',
                cancelTitle: 'NO',
                footerClass: 'p-2',
                hideHeaderClose: false,
                centered: true
              })
              .then(deleteConfirmed => {

                if (deleteConfirmed) {
                  fetch(`/api/shops/delete/${this.id}`, {
                      method: "DELETE",
                  })
                    .then((response) => response.json())
                    .then((response) => {
                      if (response.status == 404) {
                        throw new Error(response.message);
                      }

                      // remove the deleted shop's products from shopping cart
                      response.products.forEach((product) => {
                        this.$store.commit('removeProduct', product.id);
                      });

                      this.$router.push(`/app/shops/list`);
                    })
                }
              })
              .catch(err => {
                this.error = err;
              });
    }
  },
  props: ["id"],
  created: function() {
    const vm = this;
    fetch(`/api/shops/${vm.id}`)
      .then((response) => response.json())
      .then((response) => {
        if (response.status == 404) {
          throw new Error(response.message);
        }

        return response;
      })
      .then((data) => (vm.shop = data))
      .catch((error) => {
        vm.error = error;
      });
  },
};
</script>

<style scoped>
#shop-details-wrapper {
  background-color: #fbf7ed;
}

#shop-details {
  height: 680px;
}

#shop-image {
  width: 100%;
}

.shopAttribute{
  display: inline-block;
  border: #d6d6de solid 1px;
  padding: 5px;
  margin: 0 10px 0 0;
}

.shopCategory{
  background: aquamarine;
}

.shopTag{
  background: azure;
}

.card-button {
  margin-right: 0.5em;
}

.card-img-top {
  object-fit: cover;
}

</style>
