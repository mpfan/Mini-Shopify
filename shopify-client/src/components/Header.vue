<template>
  <div>
    <b-navbar toggleable="md" type="light" variant="light">
      <b-container fluid>
        <b-row cols="3" style="width:100%" no-gutters>
          <b-col align-self="start">
            <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
            <b-collapse id="nav-collapse" is-nav>
              <b-navbar-nav class="mr-auto">
                <b-nav-item
                    to="/app/shops/list"
                    @click="reload"
                >Shop</b-nav-item>
                <b-nav-item to="/app/shops/create">Create</b-nav-item>
                <b-nav-item id="cartNav" to="/app/cart">Cart {{ cartItems }}</b-nav-item>
                <b-form-input
                    @keyup.enter="submitSearch"
                    placeholder="Search"
                    v-model="keyword"
                ></b-form-input>
              </b-navbar-nav>
            </b-collapse>
          </b-col>
          <b-col align-self="center" class="text-center">
            <b-navbar-brand to="/app">MiniShopify</b-navbar-brand>
          </b-col>
          <b-col align-self="end">
            <b-collapse id="nav-collapse" is-nav>
              <!-- Right aligned nav items -->
              <b-navbar-nav class="ml-auto">
                <b-nav-item to="/app/about">About</b-nav-item>
              </b-navbar-nav>
            </b-collapse>
          </b-col>
        </b-row>
      </b-container>
    </b-navbar>
  </div>
</template>

<script>
export default {
  name: "Header",
  methods:{
    submitSearch: function(){
      if(this.keyword){
        let currRoute = this.$router.currentRoute.name;
        this.$router.push({name: 'ShopList', query:{keyword: this.keyword}});
        if(currRoute === 'ShopList'){
          this.$router.go(0);
        }
      }
    },
    reload: function(){
      if (this.$router.currentRoute.name === 'ShopList'){
        this.$router.go(0);
      }
    }
  },
  computed: {
    cartItems: function() {
      var cart_size = Object.keys(this.$store.state.cart).length;
      if (cart_size == 0) {
        return;
      } else {
        return '(' + Object.keys(this.$store.state.cart).length + ')';
      }
    }
  }
}
</script>

<style scoped>

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

#cartNav{
  width: 150px;
}

</style>