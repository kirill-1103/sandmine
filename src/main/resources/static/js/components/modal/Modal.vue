<template>
  <div class="modal-backdrop" @click="close">
    <div class="modal">
      <header class="modal-header">
        <h1 class="modal-title">{{ title }}</h1>
      </header>
      <section class="modal-body">
        <slot name="body"
        />
      </section>
      <footer class="modal-footer">
        <slot name="footer"
        />
      </footer>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Modal',
  props: {
    title: {
      type: String,
      required: false
    }
  },
  methods: {}
};
</script>

<style lang="css">
.modal-backdrop {
  position: fixed;
  z-index: 999;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  max-height: 500pt;
  position: relative;
  padding: 30px 30px 40px 30px;
  width: 40%;
  z-index: 1000;
  background: #FFFFFF;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
}

&
__title {
  font-weight: 600;
  font-size: 24px;
  display: block;
  color: var(--black-title);
  margin-bottom: 30px;
}

&
-footer {
  margin-top: 30px;
  display: flex;
  align-items: center;
}



.btn-close {
  position: absolute;
  top: 17px;
  right: 17px;
  border: none;
  cursor: pointer;
  font-weight: bold;
  color: #2196F3;
  background: transparent;
}

</style>